package com.lingxi.isi.domain.repository;

import com.lingxi.isi.domain.model.entity.InterviewQuestionRecord;

import java.util.List;
import java.util.Optional;

public interface InterviewQuestionRecordRepository {

    InterviewQuestionRecord save(InterviewQuestionRecord record);

    Optional<InterviewQuestionRecord> findById(Long id);

    List<InterviewQuestionRecord> findByRoomId(Long roomId);

    void deleteById(Long id);

    List<InterviewQuestionRecord> findAll();
}
