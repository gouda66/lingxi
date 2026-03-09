package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.common.result.R;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    @Value("${lingxi.upload-path:./uploads/}")
    private String basePath;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("文件上传: {}", file.getOriginalFilename());

        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 使用UUID重新生成文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        // 创建目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // 将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return R.error("文件上传失败");
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    public void download(@RequestParam String name, HttpServletResponse response) {
        log.info("文件下载: {}", name);

        try (FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
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
        }
    }
}
