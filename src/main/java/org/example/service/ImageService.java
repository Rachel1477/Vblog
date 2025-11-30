package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.Image;
import org.example.mapper.ImageMapper;
import org.example.util.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传下载服务（支持图片、视频等）
 */
@Service
public class ImageService {

    private static final String IMAGE_SUB_DIR = "/uploads/images/";
    private static final String VIDEO_SUB_DIR = "/uploads/videos/";
    private static final String FILE_SUB_DIR = "/uploads/files/";

    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp"
    );

    // 允许的视频类型
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mov", "video/wmv", "video/flv", "video/webm"
    );

    // 最大文件大小：50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private TokenHelper tokenHelper;

    /**
     * 处理图片上传，保存到本地并记录数据库
     */
    @Transactional
    public Image uploadImage(MultipartFile file, HttpServletRequest request) {
        return uploadFile(file, request, IMAGE_SUB_DIR, ALLOWED_IMAGE_TYPES, "图片");
    }

    /**
     * 处理视频上传，保存到本地并记录数据库
     */
    @Transactional
    public Image uploadVideo(MultipartFile file, HttpServletRequest request) {
        return uploadFile(file, request, VIDEO_SUB_DIR, ALLOWED_VIDEO_TYPES, "视频");
    }

    /**
     * 处理通用文件上传，保存到本地并记录数据库
     */
    @Transactional
    public Image uploadFile(MultipartFile file, HttpServletRequest request) {
        return uploadFile(file, request, FILE_SUB_DIR, null, "文件");
    }

    /**
     * 通用文件上传方法
     */
    private Image uploadFile(MultipartFile file, HttpServletRequest request, 
                            String subDir, List<String> allowedTypes, String fileTypeName) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        Long userId = tokenHelper.getUidFromRequest(request);
        if (userId == null) {
            throw new RuntimeException("未登录或Token无效");
        }

        // 文件大小检查
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过50MB");
        }

        // 文件类型检查
        String contentType = file.getContentType();
        if (allowedTypes != null && !allowedTypes.contains(contentType)) {
            throw new RuntimeException("不支持的文件类型：" + contentType);
        }

        // 原始文件名 & 扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 计算存储路径（相对于项目运行目录）
        String baseDir = System.getProperty("user.dir") + subDir;
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("创建上传目录失败");
        }

        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }

        String url = subDir + fileName; // 对外访问 URL

        Image image = new Image();
        image.setUserId(userId);
        image.setUrl(url);
        image.setPath(dest.getAbsolutePath());
        image.setSize(file.getSize());
        image.setContentType(contentType);

        int result = imageMapper.insert(image);
        if (result <= 0) {
            throw new RuntimeException("保存" + fileTypeName + "记录失败");
        }

        return image;
    }

    /**
     * 下载文件（带权限验证）
     */
    public ResponseEntity<Resource> downloadFile(Long fileId, HttpServletRequest request) {
        Long userId = tokenHelper.getUidFromRequest(request);
        if (userId == null) {
            throw new RuntimeException("未登录或Token无效");
        }

        Image image = imageMapper.findById(fileId);
        if (image == null) {
            throw new RuntimeException("文件不存在");
        }

        // 权限验证：只能下载自己的文件
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限下载该文件");
        }

        File file = new File(image.getPath());
        if (!file.exists()) {
            throw new RuntimeException("文件不存在或已被删除");
        }

        Resource resource = new FileSystemResource(file);
        String contentType = image.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + getFileNameFromUrl(image.getUrl()) + "\"")
                .body(resource);
    }

    /**
     * 获取用户的所有文件
     */
    public List<Image> getUserFiles(Long userId) {
        return imageMapper.findByUserId(userId);
    }

    /**
     * 根据文件类型获取用户的文件
     */
    public List<Image> getUserFilesByType(Long userId, String contentType) {
        return imageMapper.findByUserIdAndType(userId, contentType);
    }

    /**
     * 获取文件信息
     */
    public Image getFileById(Long fileId, Long userId) {
        Image image = imageMapper.findById(fileId);
        if (image == null) {
            throw new RuntimeException("文件不存在");
        }

        // 权限验证：只能查看自己的文件
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限查看该文件");
        }

        return image;
    }

    /**
     * 删除文件
     */
    @Transactional
    public boolean deleteFile(Long fileId, Long userId) {
        Image image = imageMapper.findById(fileId);
        if (image == null) {
            throw new RuntimeException("文件不存在");
        }

        // 权限验证：只能删除自己的文件
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的文件");
        }

        // 删除物理文件
        File file = new File(image.getPath());
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("删除文件失败");
            }
        }

        // 删除数据库记录
        int result = imageMapper.deleteById(fileId);
        return result > 0;
    }

    /**
     * 统计用户文件数量
     */
    public int countUserFiles(Long userId) {
        return imageMapper.countByUserId(userId);
    }

    /**
     * 从URL中提取文件名
     */
    private String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "file";
        }
        int lastIndex = url.lastIndexOf("/");
        if (lastIndex >= 0 && lastIndex < url.length() - 1) {
            return url.substring(lastIndex + 1);
        }
        return url;
    }
}


