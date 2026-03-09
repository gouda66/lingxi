package com.lingxi.isi.infrastructure.repository.jpa;

import com.lingxi.isi.domain.model.entity.AiQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAiQuestionRepository extends JpaRepository<AiQuestion, Long> {

    List<AiQuestion> findByResumeId(Long resumeId);

    List<AiQuestion> findByResumeIdAndQuestionType(Long resumeId, String questionType);

    List<AiQuestion> findByStatus(Integer status);

    List<AiQuestion> findByAiGenerated(Boolean aiGenerated);
}
