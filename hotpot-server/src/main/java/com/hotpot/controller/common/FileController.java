package com.hotpot.controller.common;

import com.hotpot.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/common")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return Result.error("文件为空");
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String newName = UUID.randomUUID().toString() + ext;

            // Resolve absolute path
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            // Save file
            Path targetPath = uploadDir.resolve(newName);
            file.transferTo(targetPath.toFile());

            Map<String, String> data = new HashMap<>();
            data.put("url", "/uploads/" + newName);
            data.put("name", originalName);
            log.info("File uploaded: {} -> {}", originalName, targetPath);
            return Result.success(data);
        } catch (IOException e) {
            log.error("Upload failed", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
