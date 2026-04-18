package com.campus.trade.gateway.controller;

import com.campus.trade.common.exception.BusinessException;
import com.campus.trade.common.response.ApiResponse;
import com.campus.trade.gateway.dto.response.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.access-url-prefix}")
    private String accessUrlPrefix;

    @PostMapping(value = "/api/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ApiResponse<FileUploadResponse>> upload(@RequestPart("file") FilePart file) {
        if (file == null || !StringUtils.hasText(file.filename())) {
            return Mono.error(new BusinessException(400, "上传文件不能为空"));
        }

        String originalFilename = file.filename();
        String suffix = "";

        if (originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return Mono.error(new BusinessException(500, "创建上传目录失败"));
        }

        Path dest = Paths.get(uploadDir, fileName);

        return file.transferTo(dest)
                .then(Mono.fromSupplier(() -> {
                    FileUploadResponse response = new FileUploadResponse();
                    response.setFileName(fileName);
                    response.setUrl("http://localhost:8080" + accessUrlPrefix + fileName);
                    return ApiResponse.success("上传成功", response);
                }));
    }
}