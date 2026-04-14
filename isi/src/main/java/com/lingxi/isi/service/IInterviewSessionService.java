package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.InterviewSession;

import java.util.List;
import java.util.Map;

public interface IInterviewSessionService extends IService<InterviewSession> {

    /**
     * 创建面试会话
     */
    R<Map<String, Object>> createSession(Long resumeId, String jobPosition, Long candidateId);

    /**
     * 获取会话详情
     */
    R<InterviewSession> getSession(Long id);

    /**
     * 获取会话列表
     */
    R<List<InterviewSession>> listSessions();

    /**
     * 关闭面试间
     */
    R<Void> closeSession(Long sessionId);

    /**
     * 获取在线用户
     */
    R<List<Map<String, Object>>> getOnlineUsers(String roomId);
}
