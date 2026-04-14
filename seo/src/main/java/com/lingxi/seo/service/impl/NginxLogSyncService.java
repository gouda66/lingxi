package com.lingxi.seo.service.impl;

import com.lingxi.seo.models.dto.LogEntry;
import com.lingxi.seo.models.dto.MatchedRule;
import com.lingxi.seo.models.entity.NginxRequestLog;
import com.lingxi.seo.models.entity.NginxRewriteRule;
import com.lingxi.seo.service.INginxRequestLogService;
import com.lingxi.seo.service.INginxRewriteRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NginxLogSyncService {

    @Autowired
    private INginxRequestLogService requestLogService;

    @Autowired
    private INginxRewriteRuleService rewriteRuleService;

    private static final String LOG_PATH = "/var/log/nginx/seo-web-access.log";
    private static final String OFFSET_FILE = "/tmp/nginx_log_offset.txt";

    // Nginx 日志正则
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+) - (\\S+) \\[(.*?)\\] \"(\\S+) (\\S+) (\\S+)\" (\\d+) (\\d+) \"(.*?)\" \"(.*?)\"(?: rt=(\\S+))?(?: uct=\"(\\S+)\")?(?: uht=\"(\\S+)\")?(?: urt=\"(\\S+)\")?"
    );

    @Scheduled(cron = "0 */1 * * * ?") // 每分钟执行一次
    public void syncNginxLogs() {
        log.info("=== 开始同步 Nginx 日志 ===");

        try {
            // 1. 从数据库查询所有启用的重写规则
            List<NginxRewriteRule> allRules = rewriteRuleService.list()
                .stream()
                .filter(rule -> rule.getIsEnabled() == 1)
                .collect(Collectors.toList());

            log.info("加载重写规则：{} 条", allRules.size());

            if (allRules.isEmpty()) {
                log.warn("数据库中没有启用的重写规则，但仍会记录所有 301 重定向");
            }

            long offset = readOffset();
            log.info("读取偏移量：{}", offset);

            if (!Files.exists(Paths.get(LOG_PATH))) {
                log.error("日志文件不存在：{}", LOG_PATH);
                return;
            }

            // 2. 读取所有日志行
            List<String> lines = Files.readAllLines(Paths.get(LOG_PATH));

            log.info("日志文件总行数：{}", lines.size());

            // 3. 解析所有日志行
            List<LogEntry> parsedLogs = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                LogEntry entry = parseSimpleLogLine(line);
                if (entry != null) {
                    entry.lineIndex = i;
                    parsedLogs.add(entry);
                }
            }

            log.info("成功解析日志条目：{} 条", parsedLogs.size());

            int count = 0;
            int skipCount = 0;
            int redirectCount = 0;
            int redirectMatchedCount = 0;

            // 4. 查找 301 重定向并匹配后续请求
            for (int i = 0; i < parsedLogs.size(); i++) {
                LogEntry currentEntry = parsedLogs.get(i);

                // 跳过已处理的行（根据偏移量）
                if (currentEntry.lineIndex * 100L < offset) {
                    continue;
                }

                // 判断是否是 301 重定向
                if (currentEntry.statusCode == 301) {
                    log.info("发现 301 重定向：{} {} (行号：{})",
                        currentEntry.method, currentEntry.requestPath, currentEntry.lineIndex);

                    // 查找后面第一个非 301 状态码的请求（最多查找 10 行）
                    LogEntry nextEntry = null;
                    for (int j = i + 1; j < Math.min(i + 10, parsedLogs.size()); j++) {
                        LogEntry candidate = parsedLogs.get(j);
                        if (candidate.statusCode != 301) {
                            nextEntry = candidate;
                            log.info("找到后续请求：{} {} (状态码：{}, 行号：{})",
                                candidate.method, candidate.requestPath, candidate.statusCode, candidate.lineIndex);
                            break;
                        }
                    }

                    if (nextEntry != null) {
                        // 尝试匹配数据库中的重写规则
                        MatchedRule matchedRule = matchRewriteRule(currentEntry.requestPath, nextEntry.requestPath, allRules);

                        if (matchedRule != null) {
                            redirectMatchedCount++;
                            log.info("匹配到重写规则：{} -> {} (规则：{})",
                                currentEntry.requestPath, nextEntry.requestPath, matchedRule.rule.getRuleName());
                        } else {
                            log.info("未匹配到数据库规则，但记录 301 重定向：{} -> {} (状态码：{})",
                                currentEntry.requestPath, nextEntry.requestPath, nextEntry.statusCode);
                        }

                        // 创建合并后的日志记录（无论是否匹配到规则都记录）
                        NginxRequestLog logEntry = createMergedLogEntry(currentEntry, nextEntry, matchedRule);

                        try {
                            requestLogService.save(logEntry);
                            count++;
                            redirectCount++;
                            log.info("保存重定向日志：{} -> {} (状态码：{}, 规则：{})",
                                currentEntry.requestPath,
                                nextEntry.requestPath,
                                nextEntry.statusCode,
                                matchedRule != null ? matchedRule.rule.getRuleName() : "无");
                        } catch (Exception e) {
                            log.error("保存日志失败：{}", logEntry, e);
                        }
                        continue; // 跳过下一个请求的正常处理
                    } else {
                        log.warn("301 重定向后未找到后续请求：{} (行号：{})", currentEntry.requestPath, currentEntry.lineIndex);
                    }

                    skipCount++;
                } else {
                    // 非 301 请求，正常处理
                    NginxRequestLog logEntry = createNormalLogEntry(currentEntry);
                    try {
                        requestLogService.save(logEntry);
                        count++;
                        log.debug("保存普通日志：{} ({})", logEntry.getRequestPath(), logEntry.getStatusCode());
                    } catch (Exception e) {
                        log.error("保存日志失败：{}", logEntry, e);
                        skipCount++;
                    }
                }
            }

            // 更新偏移量
            if (!parsedLogs.isEmpty()) {
                saveOffset((long) parsedLogs.get(parsedLogs.size() - 1).lineIndex * 100);
            }

            log.info("同步完成：成功 {} 条，跳过 {} 条，重定向 {} 条 (匹配规则：{} 条)",
                count, skipCount, redirectCount, redirectMatchedCount);

        } catch (Exception e) {
            log.error("同步失败", e);
        }

        log.info("=== Nginx 日志同步结束 ===");
    }


    /**
     * 简单解析日志行
     */
    private LogEntry parseSimpleLogLine(String line) {
        try {
            String decodedLine = new String(line.getBytes("ISO-8859-1"), "UTF-8");

            Matcher matcher = LOG_PATTERN.matcher(decodedLine);
            if (!matcher.matches()) {
                return null;
            }

            LogEntry entry = new LogEntry();
            entry.remoteAddr = matcher.group(1);
            entry.method = matcher.group(4);
            entry.requestPath = matcher.group(5);
            entry.statusCode = Integer.parseInt(matcher.group(7));
            entry.referer = matcher.group(9);
            entry.userAgent = matcher.group(10);

            // 响应时间
            if (matcher.group(14) != null && !"-".equals(matcher.group(14))) {
                double rt = Double.parseDouble(matcher.group(14));
                entry.responseTime = (long) (rt * 1000);
            } else {
                entry.responseTime = 0L;
            }

            return entry;

        } catch (Exception e) {
            log.error("解析失败：{}", line, e);
            return null;
        }
    }

    /**
     * 匹配重写规则
     */
    private MatchedRule matchRewriteRule(String originalUrl, String rewrittenUrl, List<NginxRewriteRule> rules) {
        for (NginxRewriteRule rule : rules) {
            try {
                Pattern pattern = Pattern.compile(rule.getOriginalPattern());
                Matcher matcher = pattern.matcher(originalUrl);

                if (matcher.matches()) {
                    // 构建替换后的 URL
                    String expectedRewrittenUrl = matcher.replaceAll(rule.getReplacementUrl());

                    log.debug("规则匹配：原始 URL={}, 预期替换={}, 实际替换={}",
                        originalUrl, expectedRewrittenUrl, rewrittenUrl);

                    // 检查是否与实际的 rewrittenUrl 匹配
                    if (expectedRewrittenUrl.equals(rewrittenUrl) ||
                        rewrittenUrl.contains(expectedRewrittenUrl.replace("$", "")) ||
                        expectedRewrittenUrl.contains(rewrittenUrl.replace("$", ""))) {
                        return new MatchedRule(rule, originalUrl, rewrittenUrl);
                    }
                }
            } catch (Exception e) {
                log.warn("规则匹配失败：{} - {}", rule.getRuleName(), e.getMessage());
            }
        }
        return null;
    }

    /**
     * 创建合并后的日志记录
     */
    private NginxRequestLog createMergedLogEntry(LogEntry redirectEntry, LogEntry finalEntry, MatchedRule matchedRule) {
        NginxRequestLog logEntry = new NginxRequestLog();
        logEntry.setRemoteAddr(finalEntry.remoteAddr);
        logEntry.setRequestPath(finalEntry.requestPath);
        logEntry.setMethod(finalEntry.method);
        logEntry.setStatusCode(finalEntry.statusCode);
        logEntry.setReferer(finalEntry.referer);
        logEntry.setUserAgent(finalEntry.userAgent);
        logEntry.setResponseTime(finalEntry.responseTime);

        // 设置重写信息
        logEntry.setOriginalUrl(redirectEntry.requestPath);
        logEntry.setRewrittenUrl(finalEntry.requestPath);
        logEntry.setRuleName(matchedRule.rule.getRuleName());
        logEntry.setRuleFlag(matchedRule.rule.getFlag());
        logEntry.setIsRewritten(1);

        return logEntry;
    }

    /**
     * 创建普通日志记录
     */
    private NginxRequestLog createNormalLogEntry(LogEntry entry) {
        NginxRequestLog logEntry = new NginxRequestLog();
        logEntry.setRemoteAddr(entry.remoteAddr);
        logEntry.setRequestPath(entry.requestPath);
        logEntry.setMethod(entry.method);
        logEntry.setStatusCode(entry.statusCode);
        logEntry.setReferer(entry.referer);
        logEntry.setUserAgent(entry.userAgent);
        logEntry.setResponseTime(entry.responseTime);
        logEntry.setOriginalUrl(entry.requestPath);
        logEntry.setIsRewritten(0);

        return logEntry;
    }

    private long readOffset() {
        try {
            if (Files.exists(Paths.get(OFFSET_FILE))) {
                return Long.parseLong(Files.readString(Paths.get(OFFSET_FILE)).trim());
            }
        } catch (Exception e) {
            log.error("读取偏移量失败", e);
        }
        return 0;
    }

    private void saveOffset(long offset) {
        try {
            Files.writeString(Paths.get(OFFSET_FILE), String.valueOf(offset));
        } catch (Exception e) {
            log.error("保存偏移量失败", e);
        }
    }

}
