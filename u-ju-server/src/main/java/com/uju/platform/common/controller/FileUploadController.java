package com.uju.platform.common.controller;

import com.uju.platform.common.BusinessException;
import com.uju.platform.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Tag(name = "文件模块")
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Value("${upload.path:./uploads/}")
    private String uploadPath;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;

        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(filename);
            file.transferTo(filePath.toFile());
            return Result.success("/uploads/" + filename);
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败");
        }
    }
}
