package com.lingxi.isi.infrastructure.repository.impl;

import com.lingxi.isi.domain.model.entity.Resume;
import com.lingxi.isi.domain.repository.ResumeRepository;
import com.lingxi.isi.infrastructure.repository.jpa.JpaResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepository {

    private final JpaResumeRepository jpaResumeRepository;

    @Override
    public Resume save(Resume resume) {
        return jpaResumeRepository.save(resume);
    }

    @Override
    public Optional<Resume> findById(Long id) {
        return jpaResumeRepository.findById(id);
    }

    @Override
    public List<Resume> findByUserId(Long userId) {
        return jpaResumeRepository.findByUserId(userId);
    }

    @Override
    public Optional<Resume> findTopByUserIdOrderByCreateTimeDesc(Long userId) {
        return Optional.ofNullable(jpaResumeRepository.findTopByUserIdOrderByCreateTimeDesc(userId));
    }

    @Override
    public Page<Resume> findByStatus(Integer status, Pageable pageable) {
        return jpaResumeRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Resume> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable) {
        return jpaResumeRepository.findByUserIdAndStatus(userId, status, pageable);
    }

                                                                                                                                                                                                                 @Override
    public Page<Resume> findAll(Pageable pageable) {
      return jpaResumeRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        jpaResumeRepository.deleteById(id);
    }

    @Override
    public List<Resume> findAll() {
        return jpaResumeRepository.findAll();
    }
}
