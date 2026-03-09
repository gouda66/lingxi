package com.lingxi.isi.infrastructure.repository.jpa;

import com.lingxi.isi.domain.model.entity.InterviewRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaInterviewRoomRepository extends JpaRepository<InterviewRoom, Long> {

    List<InterviewRoom> findByUserId(Long userId);

    List<InterviewRoom> findByHrUserId(Long hrUserId);

    List<InterviewRoom> findByStatus(Integer status);

    InterviewRoom findTopByResumeIdOrderByCreateTimeDesc(Long resumeId);
}
