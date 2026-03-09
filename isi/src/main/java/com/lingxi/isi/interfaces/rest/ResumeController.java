package com.lingxi.isi.interfaces.rest;

import com.lingxi.isi.application.service.ResumeApplicationService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.domain.model.entity.Resume;
import com.lingxi.isi.infrastructure.filter.LoginCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 简历管理接口
 */
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeApplicationService resumeApplicationService;

    /**
     * 上传简历
     */
    @PostMapping("/upload")
    public R<Resume> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title) throws IOException {
        
        Long userId = LoginCheckFilter.getCurrentUserId();
        Resume resume = resumeApplicationService.uploadResume(userId, file, title);
        return R.success(resume);
    }

    /**
     * 获取我的简历列表
     */
    @GetMapping("/list")
    public R<List<Resume>> getMyResumes() {
        Long userId = LoginCheckFilter.getCurrentUserId();
        List<Resume> resumes = resumeApplicationService.getUserResumes(userId);
        return R.success(resumes);
    }

    /**
     * 获取简历详情
     */
    @GetMapping("/{id}")
    public R<Resume> getResumeDetail(@PathVariable Long id) {
        Resume resume = resumeApplicationService.getResumeById(id);
        return R.success(resume);
    }

    /**
     * 删除简历
     */
    @DeleteMapping("/{id}")
    public R<Void> deleteResume(@PathVariable Long id) {
        Long userId = LoginCheckFilter.getCurrentUserId();
        resumeApplicationService.deleteResume(id, userId);
        return R.success();
    }

    /**
     * 分页查询简历（管理员）
     */
    @GetMapping("/page")
    public R<Page<Resume>> pageResumes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        
        Page<Resume> pageResult = resumeApplicationService.pageResumes(page, pageSize, status);
        return R.success(pageResult);
    }
}
