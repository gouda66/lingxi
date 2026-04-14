package com.lingxi.isi.client;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Python AI Agent 调用客户端
 */
@Component
public class PythonAiAgentClient {
    private static final String PY_HOST = "http://127.0.0.1:8000";

    /**
     * 解析简历
     */
    public Map parseResume(MultipartFile file) {
        try {
            String json = HttpUtil.createPost(PY_HOST + "/ai/parse-resume")
                    .form("file", file.getOriginalFilename(), file.getInputStream())
                    .execute().body();
            return JSONUtil.toBean(json, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("简历解析失败", e);
        }
    }

    /**
     * 生成面试问题
     */
    public List<Map<String, Object>> generateQuestions(
            String jobPosition, List<String> skills, List<String> projects) {
        Map<String, Object> params = new HashMap<>();
        params.put("jobPosition", jobPosition);
        params.put("skills", skills);
        params.put("projects", projects);

        String json = HttpUtil.createPost(PY_HOST + "/ai/generate-questions")
                .form(params)
                .execute().body();
        Map<String, Object> result = JSONUtil.toBean(json, Map.class);
        return (List<Map<String, Object>>) result.get("questions");
    }

    /**
     * 实时评分（返回六维分数）
     */
    public String getScoreLine(String question, String standardAnswer, String userAnswer) {
        Map<String, Object> param = Map.of(
                "question", question,
                "standard_answer", standardAnswer,
                "user_answer", userAnswer
        );
        String json = HttpUtil.createPost(PY_HOST + "/ai/score-answer")
                .form(param).execute().body();
        return JSONUtil.parseObj(json).getStr("scoreLine");
    }

    /**
     * 生成面试报告
     */
    public Map<String, Object> generateReport(
            String jobPosition, List<String> skills, String scoreJson, String qaHistory) {
        Map<String, Object> param = new HashMap<>();
        param.put("jobPosition", jobPosition);
        param.put("skills", skills);
        param.put("scoreJson", scoreJson);
        param.put("qaHistory", qaHistory);

        String json = HttpUtil.createPost(PY_HOST + "/ai/generate-report")
                .form(param).execute().body();
        return JSONUtil.toBean(json, Map.class);
    }
}
