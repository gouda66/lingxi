package com.lingxi.isi.models.aggregate;

import com.lingxi.isi.models.entity.SysUser;
import com.lingxi.isi.models.entity.HrProfile;
import com.lingxi.isi.models.entity.HrObservation;
import com.lingxi.isi.models.entity.HrIntervention;
import com.lingxi.isi.models.entity.InterviewSession;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * HR 聚合根 - 以 HR 为核心的业务聚合
 * </p>
 *
 * @author lingxi
 * @since 2026-03-25
 */
@Data
public class HrAggregate {
    
    /**
     * HR 用户信息
     */
    private SysUser hrUser;
    
    /**
     * HR 档案信息
     */
    private HrProfile hrProfile;
    
    /**
     * 负责的面试会话列表
     */
    private List<InterviewSession> assignedSessions;
    
    /**
     * HR 观察记录列表
     */
    private List<HrObservation> observations;
    
    /**
     * HR 干预记录列表
     */
    private List<HrIntervention> interventions;
}
