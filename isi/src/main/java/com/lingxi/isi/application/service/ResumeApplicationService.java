package com.lingxi.isi.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.isi.common.exception.CustomException;
import com.lingxi.isi.domain.model.entity.Resume;
import com.lingxi.isi.domain.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeApplicationService {

    private final ResumeRepository resumeRepository;
    
    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    private static final String UPLOAD_DIR = "uploads/resumes/";

    /**
     * 上传简历
     */
    @Transactional
    public Resume uploadResume(Long userId, MultipartFile file, String title) throws IOException {
        // 生成文件名
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;

        // 确保目录存在
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());

        // 保存文件
        Files.write(path, file.getBytes());

        // 创建简历记录
        Resume resume = new Resume();
        resume.setId(snowflakeGenerator.next());
        resume.setUserId(userId);
        resume.setTitle(title != null ? title : file.getOriginalFilename());
        resume.setFilePath(filePath);
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(getFileExtension(file.getOriginalFilename()));
        resume.setStatus(0); // 待解析

        return resumeRepository.save(resume);
    }

    /**
     * 获取用户的所有简历
     */
    public List<Resume> getUserResumes(Long userId) {
        return resumeRepository.findByUserId(userId);
    }

    /**
     * 获取简历详情
     */
    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException("简历不存在"));
    }

    /**
     * 删除简历
     */
    @Transactional
    public void deleteResume(Long id, Long operatorId) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException("简历不存在"));
        
        // TODO: 检查是否有进行中的面试
        
        resumeRepository.deleteById(id);
    }

    /**
     * 更新简历状态
     */
    @Transactional
    public void updateResumeStatus(Long id, Integer status, String contentText, String structuredData) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException("简历不存在"));
        
        resume.setStatus(status);
        if (contentText != null) {
            resume.setContentText(contentText);
        }
        if (structuredData != null) {
            resume.setStructuredData(structuredData);
        }
        resume.setUpdateTime(LocalDateTime.now());
        
        resumeRepository.save(resume);
    }

    /**
     * 分页查询简历（管理员使用）
     */
    public Page<Resume> pageResumes(int page, int pageSize, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        if (status != null) {
            return resumeRepository.findByStatus(status, pageable);
        }
        return resumeRepository.findAll(pageable);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toUpperCase();
    }
}
