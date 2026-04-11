package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.models.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 简历表 服务类
 * </p>
 */
public interface IResumeService extends IService<Resume> {

    /**
     * 分页查询简历列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param resume 查询条件
     * @return 分页结果
     */
    Page<Resume> listResumes(Integer pageNum, Integer pageSize, Resume resume);

    /**
     * 根据 ID 获取简历详情
     * @param id 简历 ID
     * @return 简历详情
     */
    Resume getResumeById(Long id);

    /**
     * 删除简历
     * @param id 简历 ID
     * @return 是否成功
     */
    Boolean deleteResume(Long id);

    /**
     * 上传并解析简历
     * @param file 简历文件
     * @return 是否成功
     */
    Boolean uploadAndParse(MultipartFile file);

    /**
     * 重新解析简历
     * @param id 简历 ID
     * @return 是否成功
     */
    Boolean reParseResume(Long id);
}
