package com.lingxi.isi.domain.service;

import com.lingxi.isi.domain.model.entity.InterviewRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 面试领域服务
 * 处理面试相关的核心业务逻辑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewDomainService {

    /**
     * 验证面试是否可以开始
     */
    public boolean canStart(InterviewRoom room) {
        if (room == null) {
            return false;
        }
        
        // 只有待开始的面试才能启动
        if (!room.getStatus().equals(0)) {
            log.warn("面试状态不是待开始，无法启动：status={}", room.getStatus());
            return false;
        }
        
        return true;
    }

    /**
     * 验证面试是否可以结束
     */
    public boolean canEnd(InterviewRoom room) {
        if (room == null) {
            return false;
        }
        
        // 只有进行中的面试才能结束
        if (!room.getStatus().equals(1)) {
            log.warn("面试状态不是进行中，无法结束：status={}", room.getStatus());
            return false;
        }
        
        return true;
    }

    /**
     * 验证 HR 是否可以加入面试
     */
    public boolean canHrJoin(InterviewRoom room, Long hrUserId) {
        if (room == null) {
            return false;
        }
        
        // 面试必须是待开始或进行中
        Integer status = room.getStatus();
        if (!status.equals(0) && !status.equals(1)) {
            log.warn("面试状态不允许加入：status={}", status);
            return false;
        }
        
        // HR 用户必须有效
        if (hrUserId == null) {
            return false;
        }
        
        return true;
    }

    /**
     * 计算面试时长（分钟）
     */
    public Integer calculateDuration(InterviewRoom room) {
        if (room.getActualStartTime() == null || room.getActualEndTime() == null) {
            return null;
        }
        
        long millis = room.getActualEndTime().toInstant(java.time.ZoneOffset.UTC)
                .toEpochMilli() - room.getActualStartTime().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        
        return (int) (millis / 1000 / 60);
    }

    /**
     * 判断面试是否超时
     */
    public boolean isOvertime(InterviewRoom room, int expectedMinutes) {
        Integer actualMinutes = calculateDuration(room);
        if (actualMinutes == null) {
            return false;
        }
        
        return actualMinutes > expectedMinutes;
    }

    /**
     * 生成面试房间号
     */
    public String generateRoomCode() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String timestamp = String.format("%d%02d%02d%02d%02d%02d",
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute(),
                now.getSecond());
        
        String random = cn.hutool.core.util.RandomUtil.randomNumbers(6).toUpperCase();
        return "ROOM-" + timestamp + "-" + random;
    }
}
