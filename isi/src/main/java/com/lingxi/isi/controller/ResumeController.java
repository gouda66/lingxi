package com.lingxi.isi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.Resume;
import com.lingxi.isi.service.IResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 简历表 前端控制器
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/interview/resume")
public class ResumeController {

    private final IResumeService resumeService;

    public ResumeController(IResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * 查询简历列表（分页）
     */
    @GetMapping("/list")
    public R<Page<Resume>> listResumes(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       Resume resume) {
        Page<Resume> result = resumeService.listResumes(pageNum, pageSize, resume);
        return R.success(result);
    }

    /**
     * 获取简历详情
     */
    @GetMapping("/{id}")
    public R<Resume> getResume(@PathVariable Long id) {
        Resume resume = resumeService.getResumeById(id);
        return R.success(resume);
    }

    /**
     * 删除简历
     */
    @DeleteMapping("/{id}")
    public R<String> deleteResume(@PathVariable Long id) {
        Boolean result = resumeService.deleteResume(id);
        if (result) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    /**
     * 上传简历
     */
    @PostMapping("/upload")
    public R<String> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            Boolean result = resumeService.uploadAndParse(file);
            if (result) {
                return R.success("上传并解析成功");
            } else {
                return R.error("上传并解析失败");
            }
        } catch (Exception e) {
            log.error("上传简历失败", e);
            return R.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 重新解析简历
     */
    @PutMapping("/reparse/{id}")
    public R<String> reParseResume(@PathVariable Long id) {
        try {
            Boolean result = resumeService.reParseResume(id);
            if (result) {
                return R.success("重新解析成功");
            } else {
                return R.error("重新解析失败");
            }
        } catch (Exception e) {
            log.error("重新解析简历失败", e);
            return R.error("重新解析失败：" + e.getMessage());
        }
    }
}
