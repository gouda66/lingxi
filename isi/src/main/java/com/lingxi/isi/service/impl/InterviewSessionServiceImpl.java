package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.mapper.InterviewSessionMapper;
import com.lingxi.isi.models.entity.InterviewSession;
import com.lingxi.isi.service.IInterviewSessionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InterviewSessionServiceImpl extends ServiceImpl<InterviewSessionMapper, InterviewSession> implements IInterviewSessionService {

    @Override
    public R<Map<String, Object>> createSession(Long resumeId, String jobPosition, Long candidateId) {
        // 生成会话码
        String sessionCode = generateSessionCode();
        
        // 创建会话
        InterviewSession session = new InterviewSession();
        session.setSessionCode(sessionCode);
        session.setResumeId(resumeId);
        session.setJobPosition(jobPosition);
        session.setCandidateId(candidateId);
        session.setStatus("CREATED");
        session.setCreatedAt(LocalDateTime.now());
        
        this.save(session);
        
        // 返回会话信息
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", session.getId());
        result.put("sessionCode", sessionCode);
        result.put("jobPosition", jobPosition);
        
        return R.success(result);
    }

    @Override
    public R<InterviewSession> getSession(Long id) {
        InterviewSession session = this.getById(id);
        if (session == null) {
            return R.error("会话不存在");
        }
        return R.success(session);
    }

    @Override
    public R<List<InterviewSession>> listSessions() {
        LambdaQueryWrapper<InterviewSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(InterviewSession::getCreatedAt);
        List<InterviewSession> list = this.list(wrapper);
        return R.success(list);
    }

    @Override
    public R<Void> closeSession(Long sessionId) {
        InterviewSession session = this.getById(sessionId);
        if (session != null) {
            session.setStatus("CLOSED");
            session.setActualEndTime(LocalDateTime.now());
            this.updateById(session);
        }
        return R.success(null);
    }

    @Override
    public R<List<Map<String, Object>>> getOnlineUsers(String roomId) {
        // TODO: 实际项目中应该从 WebSocket 会话管理中获取真实在线用户
        // 这里返回空列表作为占位
        List<Map<String, Object>> onlineUsers = new ArrayList<>();
        return R.success(onlineUsers);
    }

    /**
     * 生成会话码
     */
    private String generateSessionCode() {
        String dateStr = LocalDateTime.now().toString().substring(0, 10).replace("-", "");
        String randomStr = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "IV-" + dateStr + "-" + randomStr;
    }
}
