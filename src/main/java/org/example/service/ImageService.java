package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.entity.Image;
import org.example.mapper.ImageMapper;
import org.example.util.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 图片上传服务
 */
@Service
public class ImageService {

    private static final String IMAGE_SUB_DIR = "/uploads/images/";

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private TokenHelper tokenHelper;

    /**
     * 处理图片上传，保存到本地并记录数据库
     */
    @Transactional
    public Image uploadImage(MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        Long userId = tokenHelper.getUidFromRequest(request);
        if (userId == null) {
            throw new RuntimeException("未登录或Token无效");
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
        String baseDir = System.getProperty("user.dir") + IMAGE_SUB_DIR;
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

        String url = IMAGE_SUB_DIR + fileName; // 对外访问 URL

        Image image = new Image();
        image.setUserId(userId);
        image.setUrl(url);
        image.setPath(dest.getAbsolutePath());
        image.setSize(file.getSize());
        image.setContentType(file.getContentType());

        int result = imageMapper.insert(image);
        if (result <= 0) {
            throw new RuntimeException("保存图片记录失败");
        }

        return image;
    }
}


