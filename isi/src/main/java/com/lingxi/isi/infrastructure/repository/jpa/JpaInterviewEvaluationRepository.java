package com.lingxi.isi.infrastructure.repository.jpa;

import com.lingxi.isi.domain.model.entity.InterviewEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaInterviewEvaluationRepository extends JpaRepository<InterviewEvaluation, Long> {

    Optional<InterviewEvaluation> findByRoomId(Long roomId);

    List<InterviewEvaluation> findByUserId(Long userId);

    List<InterviewEvaluation> findByResumeId(Long resumeId);
}
