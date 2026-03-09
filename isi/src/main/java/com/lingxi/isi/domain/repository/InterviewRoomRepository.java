package com.lingxi.isi.domain.repository;

import com.lingxi.isi.domain.model.entity.InterviewRoom;

import java.util.List;
import java.util.Optional;

public interface InterviewRoomRepository {

    InterviewRoom save(InterviewRoom room);

    Optional<InterviewRoom> findById(Long id);

    Optional<InterviewRoom> findByRoomId(String roomId);

    List<InterviewRoom> findByUserId(Long userId);

    List<InterviewRoom> findByHrUserId(Long hrUserId);

    List<InterviewRoom> findByStatus(Integer status);

    List<InterviewRoom> findByResumeIdAndStatus(Long resumeId, Integer status);

    Optional<InterviewRoom> findTopByResumeIdOrderByCreateTimeDesc(Long resumeId);

    void deleteById(Long id);

    List<InterviewRoom> findAll();
}
