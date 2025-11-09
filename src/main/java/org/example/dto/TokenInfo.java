package org.example.dto;

import java.util.Date;

/**
 * Token 信息DTO
 * 用于封装从 Token 中解析出的信息
 */
public class TokenInfo {
    private Long uid;              // 用户唯一标识符
    private String username;       // 用户名
    private Date issuedAt;         // Token 签发时间（登录时间）
    private Date expiresAt;        // Token 过期时间
    private Long timestamp;        // 签发时间戳（毫秒）

    public TokenInfo() {
    }

    public TokenInfo(Long uid, String username, Date issuedAt, Date expiresAt) {
        this.uid = uid;
        this.username = username;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.timestamp = issuedAt != null ? issuedAt.getTime() : null;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
        this.timestamp = issuedAt != null ? issuedAt.getTime() : null;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                ", timestamp=" + timestamp +
                '}';
    }
}

