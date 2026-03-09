package com.lingxi.isi.infrastructure.repository.jpa;

import com.lingxi.isi.domain.model.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUserId(Long userId);

    Resume findTopByUserIdOrderByCreateTimeDesc(Long userId);

    Page<Resume> findByStatus(Integer status, Pageable pageable);

    Page<Resume> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);
}
