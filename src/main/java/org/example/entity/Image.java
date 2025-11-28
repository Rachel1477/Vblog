package org.example.entity;

import java.time.LocalDateTime;

/**
 * 图片资源实体
 */
public class Image {
    private Long id;
    private Long userId;
    private String url;          // 对外访问 URL，如 /uploads/images/xxx.png
    private String path;         // 服务器本地存储路径
    private Long size;           // 文件大小（字节）
    private String contentType;  // MIME 类型
    private LocalDateTime createTime;

    public Image() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}


