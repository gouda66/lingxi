package com.lingxi.isi.domain.repository;

import com.lingxi.isi.domain.model.entity.AiQuestion;

import java.util.List;
import java.util.Optional;

public interface AiQuestionRepository {

    AiQuestion save(AiQuestion question);

    Optional<AiQuestion> findById(Long id);

    List<AiQuestion> findByResumeId(Long resumeId);

    List<AiQuestion> findByResumeIdAndQuestionType(Long resumeId, String questionType);

    List<AiQuestion> findByStatus(Integer status);

    List<AiQuestion> findByAiGenerated(Boolean aiGenerated);

    void deleteById(Long id);

    List<AiQuestion> findAll();
}
