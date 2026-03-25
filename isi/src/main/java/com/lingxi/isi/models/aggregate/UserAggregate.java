package com.lingxi.isi.models.aggregate;

import com.lingxi.isi.models.entity.SysUser;
import com.lingxi.isi.models.entity.HrProfile;
import com.lingxi.isi.models.entity.Resume;
import com.lingxi.isi.models.entity.InterviewSession;
import com.lingxi.isi.models.entity.OperationLog;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 用户聚合根 - 以用户为核心的业务聚合
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class UserAggregate {
    
    /**
     * 用户基本信息
     */
    private SysUser user;
    
    /**
     * HR 档案信息（如果是 HR）
     */
    private HrProfile hrProfile;
    
    /**
     * 用户的简历列表
     */
    private List<Resume> resumes;
    
    /**
     * 用户参与的面试会话列表（作为面试者）
     */
    private List<InterviewSession> interviewSessions;
    
    /**
     * 用户的操作日志列表
     */
    private List<OperationLog> operationLogs;
}
