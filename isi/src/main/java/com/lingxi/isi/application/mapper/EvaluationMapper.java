package com.lingxi.isi.application.mapper;

import com.lingxi.isi.application.dto.EvaluationDTO;
import com.lingxi.isi.domain.model.entity.InterviewEvaluation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 评估 Mapper
 */
@Mapper
public interface EvaluationMapper {

    EvaluationMapper INSTANCE = Mappers.getMapper(EvaluationMapper.class);

    /**
     * Entity 转 DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "roomId", source = "roomId")
    @Mapping(target = "resumeId", source = "resumeId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "technicalSkill", source = "technicalSkill")
    @Mapping(target = "communication", source = "communication")
    @Mapping(target = "problemSolving", source = "problemSolving")
    @Mapping(target = "learningAbility", source = "learningAbility")
    @Mapping(target = "teamwork", source = "teamwork")
    @Mapping(target = "culturalFit", source = "culturalFit")
    @Mapping(target = "totalScore", source = "totalScore")
    @Mapping(target = "overallComment", source = "overallComment")
    @Mapping(target = "strengths", source = "strengths")
    @Mapping(target = "weaknesses", source = "weaknesses")
    @Mapping(target = "recommendation", source = "recommendation")
    @Mapping(target = "hrReviewed", source = "hrReviewed")
    @Mapping(target = "hrComments", source = "hrComments")
    EvaluationDTO toDTO(InterviewEvaluation evaluation);

    /**
     * DTO 转 Entity
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "roomId", source = "roomId")
    @Mapping(target = "resumeId", source = "resumeId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "technicalSkill", source = "technicalSkill")
    @Mapping(target = "communication", source = "communication")
    @Mapping(target = "problemSolving", source = "problemSolving")
    @Mapping(target = "learningAbility", source = "learningAbility")
    @Mapping(target = "teamwork", source = "teamwork")
    @Mapping(target = "culturalFit", source = "culturalFit")
    @Mapping(target = "totalScore", source = "totalScore")
    @Mapping(target = "overallComment", source = "overallComment")
    @Mapping(target = "strengths", source = "strengths")
    @Mapping(target = "weaknesses", source = "weaknesses")
    @Mapping(target = "recommendation", source = "recommendation")
    @Mapping(target = "hrReviewed", source = "hrReviewed")
    @Mapping(target = "hrComments", source = "hrComments")
    InterviewEvaluation toEntity(EvaluationDTO dto);
}
