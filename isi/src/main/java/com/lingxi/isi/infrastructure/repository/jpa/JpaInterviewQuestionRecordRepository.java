package com.lingxi.isi.infrastructure.repository.jpa;

import com.lingxi.isi.domain.model.entity.InterviewQuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaInterviewQuestionRecordRepository extends JpaRepository<InterviewQuestionRecord, Long> {

    List<InterviewQuestionRecord> findByRoomId(Long roomId);
}
