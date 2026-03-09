package com.lingxi.isi.infrastructure.repository.impl;

import com.lingxi.isi.domain.model.entity.InterviewEvaluation;
import com.lingxi.isi.domain.repository.InterviewEvaluationRepository;
import com.lingxi.isi.infrastructure.repository.jpa.JpaInterviewEvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InterviewEvaluationRepositoryImpl implements InterviewEvaluationRepository {

    private final JpaInterviewEvaluationRepository jpaRepository;

    @Override
    public InterviewEvaluation save(InterviewEvaluation evaluation) {
        return jpaRepository.save(evaluation);
    }

    @Override
    public Optional<InterviewEvaluation> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<InterviewEvaluation> findByRoomId(Long roomId) {
        return jpaRepository.findByRoomId(roomId);
    }

    @Override
    public List<InterviewEvaluation> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public List<InterviewEvaluation> findByResumeId(Long resumeId) {
        return jpaRepository.findByResumeId(resumeId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<InterviewEvaluation> findAll() {
        return jpaRepository.findAll();
    }
}
