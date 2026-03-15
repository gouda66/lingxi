package com.lingxi.isi.domain.repository;

import com.lingxi.isi.domain.model.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository {

    Resume save(Resume resume);

    Optional<Resume> findById(Long id);

    List<Resume> findByUserId(Long userId);

    Optional<Resume> findTopByUserIdOrderByCreateTimeDesc(Long userId);

    Page<Resume> findByStatus(Integer status, Pageable pageable);

    Page<Resume> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    Page<Resume> findAll(Pageable pageable);

    void deleteById(Long id);

    List<Resume> findAll();
}
