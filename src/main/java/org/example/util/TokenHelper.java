package org.example.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Token 辅助工具类
 * 提供便捷的方法从请求中获取 Token 信息
 */
@Component
public class TokenHelper {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 从请求头中提取 Token
     */
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 从请求中获取用户ID（uid）
     */
    public Long getUidFromRequest(HttpServletRequest request) {
        // 优先从请求属性中获取（拦截器已解析）
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            return (Long) userId;
        }
        
        // 如果属性中没有，则从 Token 中解析
        String token = extractToken(request);
        if (token != null) {
            return jwtUtil.getUserIdFromToken(token);
        }
        
        return null;
    }

    /**
     * 从请求中获取用户名
     */
    public String getUsernameFromRequest(HttpServletRequest request) {
        // 优先从请求属性中获取
        Object username = request.getAttribute("username");
        if (username != null) {
            return (String) username;
        }
        
        // 从 Token 中解析
        String token = extractToken(request);
        if (token != null) {
            return jwtUtil.getUsernameFromToken(token);
        }
        
        return null;
    }

    /**
     * 从请求中获取 Token 签发时间戳
     */
    public Long getLoginTimestampFromRequest(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            return jwtUtil.getIssuedAtTimestamp(token);
        }
        return null;
    }

    /**
     * 从请求中解析完整的 Token 信息
     * 包括：uid、username、登录时间、过期时间、时间戳
     */
    public TokenInfo parseTokenFromRequest(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            return jwtUtil.parseToken(token);
        }
        return null;
    }

    /**
     * 检查当前请求是否已认证（有有效的 Token）
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        return getUidFromRequest(request) != null;
    }
}

