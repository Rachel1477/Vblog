package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.annotation.RequireAuth;
import org.example.common.Result;
import org.example.entity.Image;
import org.example.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传相关接口
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private ImageService imageService;

    /**
     * 上传图片
     * 请求示例（前端）：
     * POST /api/upload/image
     * Content-Type: multipart/form-data
     * form-data 中 key 为 "file"
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAuth
    public Result<Image> uploadImage(@RequestPart("file") MultipartFile file,
                                     HttpServletRequest request) {
        try {
            Image image = imageService.uploadImage(file, request);
            return Result.success("上传成功", image);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


