package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 管理服务
 * 使用 Redis 存储 Token 和最后活动时间，实现 Token 失效机制
 */
@Service
public class TokenService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Token 前缀
    private static final String TOKEN_PREFIX = "vblog:token:";
    // 最后活动时间前缀
    private static final String LAST_ACTIVITY_PREFIX = "vblog:activity:";
    // 24小时（毫秒）
    private static final Long ONE_DAY_MS = 86400000L;

    /**
     * 保存 Token 到 Redis
     *
     * @param username 用户名
     * @param token    JWT Token
     */
    public void saveToken(String username, String token) {
        String key = TOKEN_PREFIX + username;
        String activityKey = LAST_ACTIVITY_PREFIX + username;
        
        // 保存 Token，设置过期时间
        redisTemplate.opsForValue().set(key, token, expiration, TimeUnit.MILLISECONDS);
        
        // 保存最后活动时间
        redisTemplate.opsForValue().set(activityKey, System.currentTimeMillis(), expiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取 Token
     *
     * @param username 用户名
     * @return Token
     */
    public String getToken(String username) {
        String key = TOKEN_PREFIX + username;
        Object token = redisTemplate.opsForValue().get(key);
        return token != null ? token.toString() : null;
    }

    /**
     * 删除 Token（登出）
     *
     * @param username 用户名
     */
    public void deleteToken(String username) {
        String key = TOKEN_PREFIX + username;
        String activityKey = LAST_ACTIVITY_PREFIX + username;
        redisTemplate.delete(key);
        redisTemplate.delete(activityKey);
    }

    /**
     * Token 是否存在于 Redis（是否有效）
     *
     * @param username 用户名
     * @param token    Token
     * @return 是否有效
     */
    public boolean isTokenValid(String username, String token) {
        String storedToken = getToken(username);
        if (storedToken == null || !storedToken.equals(token)) {
            return false;
        }
        
        // 检查是否24小时未活动
        if (isInactiveFor24Hours(username)) {
            deleteToken(username);
            return false;
        }
        
        return true;
    }

    /**
     * 更新最后活动时间
     *
     * @param username 用户名
     */
    public void updateLastActivity(String username) {
        String activityKey = LAST_ACTIVITY_PREFIX + username;
        redisTemplate.opsForValue().set(activityKey, System.currentTimeMillis(), expiration, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查是否24小时未活动
     *
     * @param username 用户名
     * @return 是否超过24小时未活动
     */
    public boolean isInactiveFor24Hours(String username) {
        String activityKey = LAST_ACTIVITY_PREFIX + username;
        Object lastActivity = redisTemplate.opsForValue().get(activityKey);
        
        if (lastActivity == null) {
            return true;
        }
        
        Long lastActivityTime = Long.parseLong(lastActivity.toString());
        Long currentTime = System.currentTimeMillis();
        
        return (currentTime - lastActivityTime) > ONE_DAY_MS;
    }

    /**
     * 刷新 Token（更新过期时间）
     *
     * @param username 用户名
     * @param newToken 新 Token
     */
    public void refreshToken(String username, String newToken) {
        deleteToken(username);
        saveToken(username, newToken);
    }

    /**
     * 获取最后活动时间
     *
     * @param username 用户名
     * @return 最后活动时间戳
     */
    public Long getLastActivity(String username) {
        String activityKey = LAST_ACTIVITY_PREFIX + username;
        Object lastActivity = redisTemplate.opsForValue().get(activityKey);
        return lastActivity != null ? Long.parseLong(lastActivity.toString()) : null;
    }
}

