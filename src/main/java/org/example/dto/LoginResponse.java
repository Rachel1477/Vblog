package org.example.dto;

import org.example.entity.User;

/**
 * 登录响应DTO（包含Token）
 */
public class LoginResponse {
    private String token;
    private User user;
    private Long expiresIn; // Token有效期（毫秒）

    public LoginResponse() {
    }

    public LoginResponse(String token, User user, Long expiresIn) {
        this.token = token;
        this.user = user;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", user=" + user +
                ", expiresIn=" + expiresIn +
                '}';
    }
}

