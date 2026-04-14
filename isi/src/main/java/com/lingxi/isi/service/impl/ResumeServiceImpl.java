package com.lingxi.isi.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.context.BaseContext;
import com.lingxi.isi.mapper.ResumeMapper;
import com.lingxi.isi.mapper.ResumeProjectMapper;
import com.lingxi.isi.mapper.ResumeSkillMapper;
import com.lingxi.isi.models.entity.Resume;
import com.lingxi.isi.models.entity.ResumeProject;
import com.lingxi.isi.models.entity.ResumeSkill;
import com.lingxi.isi.service.IResumeService;
import com.lingxi.isi.utils.OssUploadUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 简历表 服务实现类
 * </p>
 */
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements IResumeService {

    private static final String AI_SERVICE_URL = "http://localhost:8000";
    private static final String OSS_BUCKET = "hellolingxi";
    private static final String OSS_BASE_URL = "https://hellolingxi.oss-cn-beijing.aliyuncs.com/";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "oss-upload-temp";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ResumeProjectMapper resumeProjectMapper;
    private final ResumeSkillMapper resumeSkillMapper;

    public ResumeServiceImpl(ResumeProjectMapper resumeProjectMapper, ResumeSkillMapper resumeSkillMapper) {
        this.resumeProjectMapper = resumeProjectMapper;
        this.resumeSkillMapper = resumeSkillMapper;
    }

    @Override
    public Page<Resume> listResumes(Integer pageNum, Integer pageSize, Resume resume) {
        Page<Resume> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        if (resume != null) {
            wrapper.like(StringUtils.hasText(resume.getFullName()), Resume::getFullName, resume.getFullName())
                   .like(StringUtils.hasText(resume.getPhone()), Resume::getPhone, resume.getPhone())
                   .like(StringUtils.hasText(resume.getJobTitle()), Resume::getJobTitle, resume.getJobTitle());
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(Resume::getCreatedAt);
        
        return this.page(page, wrapper);
    }

    @Override
    public Resume getResumeById(Long id) {
        return this.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteResume(Long id) {
        try {
            // 检查简历是否存在
            Resume resume = this.getById(id);
            if (resume == null) {
                return false;
            }
            
            // 删除关联的技能记录
            LambdaQueryWrapper<ResumeSkill> skillWrapper = new LambdaQueryWrapper<>();
            skillWrapper.eq(ResumeSkill::getResumeId, id);
            resumeSkillMapper.delete(skillWrapper);
            
            // 删除关联的项目记录
            LambdaQueryWrapper<ResumeProject> projectWrapper = new LambdaQueryWrapper<>();
            projectWrapper.eq(ResumeProject::getResumeId, id);
            resumeProjectMapper.delete(projectWrapper);
            
            // 删除简历主表记录
            return this.removeById(id);
        } catch (Exception e) {
            throw new RuntimeException("删除简历失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean uploadAndParse(MultipartFile file) {
        File tempFile = null;
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("上传文件为空");
            }

            // 准备文件和路径
            String originalFilename = file.getOriginalFilename();
            String fileType = getFileType(originalFilename);
            String uniqueId = UUID.randomUUID().toString().replace("-", "");
            String ossObjectKey = "isi/resume/" + uniqueId + "_" + originalFilename;
            
            tempFile = saveToTempDir(file, uniqueId, originalFilename);

            // 创建并保存简历记录
            Resume resume = createResumeRecord(originalFilename, fileType, file.getSize());
            this.save(resume);

            // 解析简历并上传到OSS
            processResumeParsing(tempFile, resume, ossObjectKey);
            
            return true;
        } catch (IOException e) {
            deleteQuietly(tempFile);
            throw new RuntimeException("上传失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reParseResume(Long id) {
        File tempFile = null;
        try {
            Resume resume = this.getById(id);
            if (resume == null) {
                throw new RuntimeException("简历不存在");
            }

            // 从 OSS 下载文件到临时目录
            String ossUrl = resume.getOriginalFileUrl();
            if (!StringUtils.hasText(ossUrl) || "pending".equals(ossUrl)) {
                throw new RuntimeException("简历文件不存在");
            }
            
            String originalFilename = resume.getResumeName();
            String uniqueId = UUID.randomUUID().toString().replace("-", "");
            tempFile = downloadFromOssToTemp(ossUrl, uniqueId, originalFilename);

            // 删除旧的关联数据
            deleteOldRelatedData(id);

            // 重置简历状态
            resume.setParseStatus(1);
            resume.setParseErrorMsg(null);
            resume.setUpdatedAt(LocalDateTime.now());
            
            // 清空基础信息字段（保留文件名、类型等元数据）
            clearResumeBasicInfo(resume);

            // 重新解析简历
            processResumeParsing(tempFile, resume, extractOssObjectKey(ossUrl));
            
            return true;
        } catch (Exception e) {
            deleteQuietly(tempFile);
            throw new RuntimeException("重新解析失败：" + e.getMessage(), e);
        }
    }

    /**
     * 保存文件到临时目录
     */
    private File saveToTempDir(MultipartFile file, String uniqueId, String originalFilename) throws IOException {
        File tempDirFile = new File(TEMP_DIR);
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }
        File tempFile = new File(TEMP_DIR + File.separator + uniqueId + "_" + originalFilename);
        file.transferTo(tempFile);
        return tempFile;
    }

    /**
     * 创建简历记录
     */
    private Resume createResumeRecord(String filename, String fileType, long fileSize) {
        Resume resume = new Resume();
        resume.setResumeName(filename);
        resume.setFileType(fileType);
        resume.setFileSize((int) fileSize);
        resume.setOriginalFileUrl("pending");
        resume.setParseStatus(1);
        resume.setIsDefault(0);
        resume.setUserId(BaseContext.getCurrentId());
        resume.setVersion(1);
        return resume;
    }

    /**
     * 处理简历解析和OSS上传
     */
    private void processResumeParsing(File tempFile, Resume resume, String ossObjectKey) {
        try {
            // 调用AI解析
            parseResumeWithAi(tempFile, resume);
            
            // 上传到OSS
            String tempFilePath = tempFile.getAbsolutePath();
            if (!OssUploadUtils.uploadFile(OSS_BUCKET, ossObjectKey, tempFilePath)) {
                throw new RuntimeException("文件上传到 OSS 失败");
            }
            
            // 更新为真实URL和成功状态
            resume.setOriginalFileUrl(OSS_BASE_URL + ossObjectKey);
            resume.setParseStatus(2);
        } catch (Exception e) {
            resume.setParseStatus(3);
            resume.setParseErrorMsg(e.getMessage());
        } finally {
            deleteQuietly(tempFile);
        }
        this.updateById(resume);
    }

    /**
     * 静默删除文件
     */
    private void deleteQuietly(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * 从 OSS 下载文件到临时目录
     */
    private File downloadFromOssToTemp(String ossUrl, String uniqueId, String originalFilename) throws IOException {
        File tempDirFile = new File(TEMP_DIR);
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }
        
        // 提取 OSS Object Key
        String objectKey = extractOssObjectKey(ossUrl);
        
        // 生成本地临时文件路径
        String localFilePath = TEMP_DIR + File.separator + uniqueId + "_" + originalFilename;
        
        // 使用 OSS SDK 下载文件
        if (!OssUploadUtils.downloadFile(OSS_BUCKET, objectKey, localFilePath)) {
            throw new IOException("从 OSS 下载文件失败");
        }
        
        return new File(localFilePath);
    }

    /**
     * 删除旧的关联数据（技能和项目）
     */
    private void deleteOldRelatedData(Long resumeId) {
        // 删除关联的技能记录
        LambdaQueryWrapper<ResumeSkill> skillWrapper = new LambdaQueryWrapper<>();
        skillWrapper.eq(ResumeSkill::getResumeId, resumeId);
        resumeSkillMapper.delete(skillWrapper);
        
        // 删除关联的项目记录
        LambdaQueryWrapper<ResumeProject> projectWrapper = new LambdaQueryWrapper<>();
        projectWrapper.eq(ResumeProject::getResumeId, resumeId);
        resumeProjectMapper.delete(projectWrapper);
    }

    /**
     * 清空简历基础信息（保留元数据）
     */
    private void clearResumeBasicInfo(Resume resume) {
        resume.setFullName(null);
        resume.setPhone(null);
        resume.setEmail(null);
        resume.setLocation(null);
        resume.setGender(null);
        resume.setBirthDate(null);
        resume.setJobTitle(null);
        resume.setExpectedSalary(null);
        resume.setWorkYears(null);
        resume.setEducationJson(null);
        resume.setSelfEvaluation(null);
    }

    /**
     * 从 OSS URL 提取 Object Key
     */
    private String extractOssObjectKey(String ossUrl) {
        if (!StringUtils.hasText(ossUrl)) {
            return "";
        }
        // URL 格式: https://hellolingxi.oss-cn-beijing.aliyuncs.com/isi/resume/xxx.pdf
        int bucketEndIndex = ossUrl.indexOf(".aliyuncs.com/");
        if (bucketEndIndex > 0) {
            return ossUrl.substring(bucketEndIndex + 14); // 跳过 ".aliyuncs.com/"
        }
        return ossUrl;
    }
    private String getFileType(String filename) {
        if (filename == null) {
            return "UNKNOWN";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "UNKNOWN";
    }

    /**
     * 调用 AI 服务解析简历
     */
    private void parseResumeWithAi(File file, Resume resume) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));

            ResponseEntity<Map> response = restTemplate.exchange(
                    AI_SERVICE_URL + "/ai/parse-resume",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                fillBasicInfo(resume, data);
                fillEducation(resume, data);
                fillSkills(resume, data);
                fillProjects(resume, data);
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 简历解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 填充基础信息
     */
    private void fillBasicInfo(Resume resume, Map<String, Object> data) {
        setIfPresent(resume::setFullName, data, "fullName");
        setIfPresent(resume::setPhone, data, "phone");
        setIfPresent(resume::setEmail, data, "email");
        setIfPresent(resume::setLocation, data, "location");
        setIfPresent(resume::setJobTitle, data, "jobTitle");
        setIfPresent(resume::setExpectedSalary, data, "expectedSalary");
        setIfPresent(resume::setSelfEvaluation, data, "selfEvaluation");
        
        // 性别转换
        String gender = (String) data.get("gender");
        if ("男".equals(gender)) {
            resume.setGender(1);
        } else if ("女".equals(gender)) {
            resume.setGender(0);
        }
        
        // 日期字段
        setIfPresentDate(resume::setBirthDate, data, "birthDate");
        
        // 工作年限
        Integer workYears = (Integer) data.get("workYears");
        if (workYears != null) {
            resume.setWorkYears(workYears);
        }
    }

    /**
     * 填充教育背景
     */
    private void fillEducation(Resume resume, Map<String, Object> data) {
        List<Map<String, Object>> educationList = (List<Map<String, Object>>) data.get("educationJson");
        if (educationList != null && !educationList.isEmpty()) {
            resume.setEducationJson(JSON.toJSONString(educationList));
        }
    }

    /**
     * 填充技能列表
     */
    private void fillSkills(Resume resume, Map<String, Object> data) {
        List<String> skills = (List<String>) data.get("skills");
        if (skills != null) {
            skills.stream()
                .filter(StringUtils::hasText)
                .forEach(skillName -> {
                    ResumeSkill resumeSkill = new ResumeSkill();
                    resumeSkill.setResumeId(resume.getId());
                    resumeSkill.setSkillName(skillName.trim());
                    resumeSkillMapper.insert(resumeSkill);
                });
        }
    }

    /**
     * 填充项目经验
     */
    private void fillProjects(Resume resume, Map<String, Object> data) {
        List<Map<String, Object>> projects = (List<Map<String, Object>>) data.get("projects");
        if (projects != null) {
            projects.forEach(projectData -> {
                ResumeProject project = new ResumeProject();
                project.setResumeId(resume.getId());
                setIfPresent(project::setProjectName, projectData, "projectName");
                setIfPresent(project::setRole, projectData, "role");
                setIfPresentDate(project::setStartDate, projectData, "startDate");
                setIfPresentDate(project::setEndDate, projectData, "endDate");
                setIfPresent(project::setDescription, projectData, "description");
                setIfPresent(project::setResponsibilities, projectData, "responsibilities");
                setIfPresent(project::setTechnologies, projectData, "technologies");
                resumeProjectMapper.insert(project);
            });
        }
    }

    /**
     * 如果值存在则设置
     */
    private void setIfPresent(java.util.function.Consumer<String> setter, Map<String, Object> data, String key) {
        String value = (String) data.get(key);
        if (StringUtils.hasText(value)) {
            setter.accept(value);
        }
    }

    /**
     * 如果日期值存在则解析并设置
     */
    private void setIfPresentDate(java.util.function.Consumer<LocalDate> setter, Map<String, Object> data, String key) {
        String dateStr = (String) data.get(key);
        if (StringUtils.hasText(dateStr)) {
            LocalDate date = parseDate(dateStr);
            if (date != null) {
                setter.accept(date);
            }
        }
    }

    /**
     * 解析日期字符串，支持多种格式
     *
     * @param dateStr 日期字符串，如：1995-06-15、1995年6月、2018-09、2018年9月
     * @return LocalDate 对象
     */
    private LocalDate parseDate(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }

        // 尝试多种日期格式
        String[] patterns = {
            "yyyy-MM-dd",
            "yyyy年MM月dd日",
            "yyyy年MM月",
            "yyyy-MM",
            "yyyy/MM/dd",
            "yyyy/MM"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                // 对于只有年份和月份的格式，默认日为1号
                if (pattern.equals("yyyy年MM月") || pattern.equals("yyyy-MM") || pattern.equals("yyyy/MM")) {
                    return LocalDate.parse(dateStr + "-01", DateTimeFormatter.ofPattern(pattern + "-dd"));
                }
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {
                // 尝试下一个格式
            }
        }

        return null;
    }

}
