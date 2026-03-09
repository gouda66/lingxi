package com.lingxi.isi.infrastructure.repository.impl;

import com.lingxi.isi.domain.model.entity.AiQuestion;
import com.lingxi.isi.domain.repository.AiQuestionRepository;
import com.lingxi.isi.infrastructure.repository.jpa.JpaAiQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AiQuestionRepositoryImpl implements AiQuestionRepository {

    private final JpaAiQuestionRepository jpaRepository;

    @Override
    public AiQuestion save(AiQuestion question) {
        return jpaRepository.save(question);
    }

    @Override
    public Optional<AiQuestion> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<AiQuestion> findByResumeId(Long resumeId) {
        return jpaRepository.findByResumeId(resumeId);
    }

    @Override
    public List<AiQuestion> findByResumeIdAndQuestionType(Long resumeId, String questionType) {
        return jpaRepository.findByResumeIdAndQuestionType(resumeId, questionType);
    }

    @Override
    public List<AiQuestion> findByStatus(Integer status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public List<AiQuestion> findByAiGenerated(Boolean aiGenerated) {
        return jpaRepository.findByAiGenerated(aiGenerated);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<AiQuestion> findAll() {
        return jpaRepository.findAll();
    }
}
