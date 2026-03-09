package com.lingxi.isi.domain.repository;

import com.lingxi.isi.domain.model.entity.InterviewEvaluation;

import java.util.List;
import java.util.Optional;

public interface InterviewEvaluationRepository {

    InterviewEvaluation save(InterviewEvaluation evaluation);

    Optional<InterviewEvaluation> findById(Long id);

    Optional<InterviewEvaluation> findByRoomId(Long roomId);

    List<InterviewEvaluation> findByUserId(Long userId);

    List<InterviewEvaluation> findByResumeId(Long resumeId);

    void deleteById(Long id);

    List<InterviewEvaluation> findAll();
}
