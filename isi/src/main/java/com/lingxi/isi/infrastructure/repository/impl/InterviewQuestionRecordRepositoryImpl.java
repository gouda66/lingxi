package com.lingxi.isi.infrastructure.repository.impl;

import com.lingxi.isi.domain.model.entity.InterviewQuestionRecord;
import com.lingxi.isi.domain.repository.InterviewQuestionRecordRepository;
import com.lingxi.isi.infrastructure.repository.jpa.JpaInterviewQuestionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InterviewQuestionRecordRepositoryImpl implements InterviewQuestionRecordRepository {

    private final JpaInterviewQuestionRecordRepository jpaRepository;

    @Override
    public InterviewQuestionRecord save(InterviewQuestionRecord record) {
        return jpaRepository.save(record);
    }

    @Override
    public Optional<InterviewQuestionRecord> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<InterviewQuestionRecord> findByRoomId(Long roomId) {
        return jpaRepository.findByRoomId(roomId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<InterviewQuestionRecord> findAll() {
        return jpaRepository.findAll();
    }
}
