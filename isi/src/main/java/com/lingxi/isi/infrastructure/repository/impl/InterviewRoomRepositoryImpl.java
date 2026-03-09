package com.lingxi.isi.infrastructure.repository.impl;

import com.lingxi.isi.domain.model.entity.InterviewRoom;
import com.lingxi.isi.domain.repository.InterviewRoomRepository;
import com.lingxi.isi.infrastructure.repository.jpa.JpaInterviewRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InterviewRoomRepositoryImpl implements InterviewRoomRepository {

    private final JpaInterviewRoomRepository jpaInterviewRoomRepository;

    @Override
    public InterviewRoom save(InterviewRoom room) {
        return jpaInterviewRoomRepository.save(room);
    }

    @Override
    public Optional<InterviewRoom> findById(Long id) {
        return jpaInterviewRoomRepository.findById(id);
    }

    @Override
    public Optional<InterviewRoom> findByRoomId(String roomId) {
        List<InterviewRoom> rooms = jpaInterviewRoomRepository.findAll();
        return rooms.stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .findFirst();
    }

    @Override
    public List<InterviewRoom> findByUserId(Long userId) {
        return jpaInterviewRoomRepository.findByUserId(userId);
    }

    @Override
    public List<InterviewRoom> findByHrUserId(Long hrUserId) {
        return jpaInterviewRoomRepository.findByHrUserId(hrUserId);
    }

    @Override
    public List<InterviewRoom> findByStatus(Integer status) {
        return jpaInterviewRoomRepository.findByStatus(status);
    }

    @Override
    public List<InterviewRoom> findByResumeIdAndStatus(Long resumeId, Integer status) {
        return jpaInterviewRoomRepository.findAll().stream()
                .filter(r -> r.getResumeId().equals(resumeId) && r.getStatus().equals(status))
                .toList();
    }

    @Override
    public Optional<InterviewRoom> findTopByResumeIdOrderByCreateTimeDesc(Long resumeId) {
        return Optional.ofNullable(
                jpaInterviewRoomRepository.findTopByResumeIdOrderByCreateTimeDesc(resumeId));
    }

    @Override
    public void deleteById(Long id) {
        jpaInterviewRoomRepository.deleteById(id);
    }

    @Override
    public List<InterviewRoom> findAll() {
        return jpaInterviewRoomRepository.findAll();
    }
}
