package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.annotation.RequireAuth;
import org.example.common.Result;
import org.example.entity.Image;
import org.example.service.ImageService;
import org.example.util.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传下载相关接口
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private TokenHelper tokenHelper;

    /**
     * 上传图片
     * 支持格式：JPEG, PNG, GIF, WEBP, BMP
     * 最大大小：50MB
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAuth
    public Result<Image> uploadImage(@RequestPart("file") MultipartFile file,
                                     HttpServletRequest request) {
        try {
            Image image = imageService.uploadImage(file, request);
            return Result.success("图片上传成功", image);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传视频
     * 支持格式：MP4, AVI, MOV, WMV, FLV, WEBM
     * 最大大小：50MB
     */
    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAuth
    public Result<Image> uploadVideo(@RequestPart("file") MultipartFile file,
                                     HttpServletRequest request) {
        try {
            Image image = imageService.uploadVideo(file, request);
            return Result.success("视频上传成功", image);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传通用文件
     * 支持所有文件类型
     * 最大大小：50MB
     */
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAuth
    public Result<Image> uploadFile(@RequestPart("file") MultipartFile file,
                                    HttpServletRequest request) {
        try {
            Image image = imageService.uploadFile(file, request);
            return Result.success("文件上传成功", image);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 下载文件（带权限验证）
     */
    @GetMapping("/download/{id}")
    @RequireAuth
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id,
                                                HttpServletRequest request) {
        try {
            return imageService.downloadFile(id, request);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/{id}")
    @RequireAuth
    public Result<Image> getFileInfo(@PathVariable Long id,
                                    HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            Image image = imageService.getFileById(id, userId);
            return Result.success(image);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我的所有文件列表
     */
    @GetMapping("/my")
    @RequireAuth
    public Result<List<Image>> getMyFiles(HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            List<Image> files = imageService.getUserFiles(userId);
            return Result.success("获取成功", files);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据文件类型获取我的文件列表
     * 参数：type - 文件类型前缀，如 "image" 或 "video"
     */
    @GetMapping("/my/type")
    @RequireAuth
    public Result<List<Image>> getMyFilesByType(@RequestParam(required = false) String type,
                                                HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            List<Image> files = imageService.getUserFilesByType(userId, type);
            return Result.success("获取成功", files);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    @RequireAuth
    public Result<Boolean> deleteFile(@PathVariable Long id,
                                     HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            boolean success = imageService.deleteFile(id, userId);
            return Result.success("文件删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 统计我的文件数量
     */
    @GetMapping("/my/count")
    @RequireAuth
    public Result<Integer> countMyFiles(HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            int count = imageService.countUserFiles(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


