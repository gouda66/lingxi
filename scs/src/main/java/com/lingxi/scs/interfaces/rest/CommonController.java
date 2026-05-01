package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.common.result.R;
import com.lingxi.scs.common.util.OssUploadUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传和下载
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${lingxi.upload-path}")
    private String basePath;

    @Value("${lingxi.oss.bucket-name}")
    private String bucketName;

    @Value("${lingxi.oss.endpoint}")
    private String ossEndpoint;

    /**
     * 文件上传到阿里云 OSS
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("文件上传: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return R.error("上传文件不能为空");
        }

        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成 OSS 存储路径：yyyy/MM/dd/UUID + suffix
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        String objectKey = "scs/" + datePath + "/" + fileName;

        try {
            // 创建临时文件
            String tempDir = System.getProperty("java.io.tmpdir");
            String tempFileName = UUID.randomUUID().toString() + suffix;
            File tempFile = new File(tempDir, tempFileName);
            
            // 保存上传的文件到临时目录
            file.transferTo(tempFile);
            log.info("临时文件已保存: {}", tempFile.getAbsolutePath());

            // 调用 OSS 上传工具类
            boolean uploadSuccess = OssUploadUtils.uploadFile(bucketName, objectKey, tempFile.getAbsolutePath());
            
            // 删除临时文件
            if (tempFile.exists()) {
                tempFile.delete();
            }

            if (uploadSuccess) {
                // 返回 OSS 完整 URL
                String ossUrl = "https://" + bucketName + "." + ossEndpoint.replace("https://", "") + "/" + objectKey;
                log.info("文件上传成功，OSS URL: {}", ossUrl);
                return R.success(ossUrl);
            } else {
                return R.error("文件上传到 OSS 失败");
            }
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return R.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("OSS 上传异常", e);
            return R.error("文件上传异常: " + e.getMessage());
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(@RequestParam String name, HttpServletResponse response) {
        log.info("文件下载: {}", name);

        File file = new File(basePath + name);
        
        // 如果文件不存在，返回默认图片
        if (!file.exists()) {
            log.warn("文件不存在: {}，返回默认图片", name);
            // 尝试返回默认图片
            String defaultImagePath = basePath + "default.png";
            File defaultFile = new File(defaultImagePath);
            if (defaultFile.exists()) {
                file = defaultFile;
            } else {
                // 如果连默认图片都没有，直接返回
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];
            int len;
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

        } catch (Exception e) {
            log.error("文件下载失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
