package com.lingxi.isi.models.aggregate;

import com.lingxi.isi.models.entity.*;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 面试会话聚合根 - 核心业务聚合
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class InterviewSessionAggregate {
    
    /**
     * 面试会话基本信息
     */
    private InterviewSession session;
    
    /**
     * 面试者信息
     */
    private SysUser candidate;
    
    /**
     * 使用的简历信息
     */
    private Resume resume;
    
    /**
     * 负责 HR 信息
     */
    private SysUser hrUser;
    
    /**
     * HR 档案信息
     */
    private HrProfile hrProfile;
    
    /**
     * 面试题目列表
     */
    private List<InterviewQuestion> questions;
    
    /**
     * 面试回答列表
     */
    private List<InterviewAnswer> answers;
    
    /**
     * HR 观察记录列表
     */
    private List<HrObservation> observations;
    
    /**
     * HR 干预记录列表
     */
    private List<HrIntervention> interventions;
    
    /**
     * 面试报告
     */
    private InterviewReport report;
    
    /**
     * 面试决策
     */
    private InterviewDecision decision;
    
    /**
     * AI 调用日志列表
     */
    private List<AiInvocationLog> aiLogs;
    
    /**
     * 发送的邮件记录列表
     */
    private List<EmailRecord> emailRecords;
}
