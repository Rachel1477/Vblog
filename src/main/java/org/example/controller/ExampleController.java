package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.annotation.RequireAuth;
import org.example.common.Result;
import org.example.dto.TokenInfo;
import org.example.util.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Token 解析使用示例 Controller
 * 演示如何从 Token 中获取 uid 和登录时间
 */
@RestController
@RequestMapping("/api/example")
public class ExampleController {

    @Autowired
    private TokenHelper tokenHelper;

    /**
     * 示例1: 从请求属性获取用户信息（最简单）
     * 拦截器已经解析并存入请求属性
     */
    @PostMapping("/method1")
    @RequireAuth
    public Result<Map<String, Object>> method1(HttpServletRequest request) {
        // 从请求属性直接获取（拦截器已解析）
        Long uid = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        
        // 如果需要登录时间，使用 TokenHelper
        Long loginTimestamp = tokenHelper.getLoginTimestampFromRequest(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("method", "从请求属性获取");
        result.put("uid", uid);
        result.put("username", username);
        result.put("loginTimestamp", loginTimestamp);
        
        return Result.success("方法1: 从请求属性获取（推荐）", result);
    }

    /**
     * 示例2: 使用 TokenHelper 工具类（推荐）
     * 一次性获取所有信息
     */
    @PostMapping("/method2")
    @RequireAuth
    public Result<Map<String, Object>> method2(HttpServletRequest request) {
        // 使用 TokenHelper 一次性获取所有信息
        TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("method", "使用 TokenHelper");
        result.put("uid", tokenInfo.getUid());
        result.put("username", tokenInfo.getUsername());
        result.put("loginTimestamp", tokenInfo.getTimestamp());
        result.put("issuedAt", tokenInfo.getIssuedAt());
        result.put("expiresAt", tokenInfo.getExpiresAt());
        
        return Result.success("方法2: 使用 TokenHelper（推荐）", result);
    }

    /**
     * 示例3: 业务场景 - 创建记录时使用 uid 和时间戳
     */
    @PostMapping("/create-record")
    @RequireAuth
    public Result<Map<String, Object>> createRecord(
            @RequestBody Map<String, Object> data,
            HttpServletRequest request) {
        
        // 获取 Token 信息
        TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
        
        // 模拟创建记录
        Map<String, Object> record = new HashMap<>();
        record.put("id", System.currentTimeMillis());  // 模拟ID
        record.put("userId", tokenInfo.getUid());      // 使用 uid
        record.put("creatorName", tokenInfo.getUsername());
        record.put("content", data.get("content"));
        record.put("createTime", System.currentTimeMillis());
        record.put("loginTime", tokenInfo.getTimestamp());  // 记录登录时间
        
        return Result.success("记录创建成功（使用了 uid 和 timestamp）", record);
    }

    /**
     * 示例4: 权限验证场景
     * 验证当前用户是否有权限操作某个资源
     */
    @PutMapping("/update-record/{id}")
    @RequireAuth
    public Result<String> updateRecord(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data,
            HttpServletRequest request) {
        
        // 获取当前用户的 uid
        Long currentUid = tokenHelper.getUidFromRequest(request);
        
        // 模拟查询记录的所有者
        Long recordOwnerId = 1L;  // 假设从数据库查询得到
        
        // 权限验证：只能修改自己的记录
        if (!currentUid.equals(recordOwnerId)) {
            return Result.error(403, "无权限：只能修改自己的记录");
        }
        
        // 执行更新操作
        return Result.success("更新成功", null);
    }

    /**
     * 示例5: 统计分析场景
     * 计算用户登录时长等信息
     */
    @GetMapping("/user-stats")
    @RequireAuth
    public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
        TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
        
        // 计算登录时长
        long currentTime = System.currentTimeMillis();
        long loginDuration = currentTime - tokenInfo.getTimestamp();
        long durationMinutes = loginDuration / 1000 / 60;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("uid", tokenInfo.getUid());
        stats.put("username", tokenInfo.getUsername());
        stats.put("loginTime", tokenInfo.getIssuedAt());
        stats.put("loginDuration", loginDuration);
        stats.put("loginDurationMinutes", durationMinutes);
        stats.put("tokenExpiresAt", tokenInfo.getExpiresAt());
        
        return Result.success("用户统计信息", stats);
    }

    /**
     * 示例6: 日志记录场景
     * 记录操作日志时使用 uid 和时间戳
     */
    @PostMapping("/log-operation")
    @RequireAuth
    public Result<Map<String, Object>> logOperation(
            @RequestBody Map<String, Object> operation,
            HttpServletRequest request) {
        
        TokenInfo tokenInfo = tokenHelper.parseTokenFromRequest(request);
        
        // 构建日志记录
        Map<String, Object> log = new HashMap<>();
        log.put("logId", System.currentTimeMillis());
        log.put("userId", tokenInfo.getUid());           // 操作者ID
        log.put("username", tokenInfo.getUsername());    // 操作者用户名
        log.put("operation", operation.get("action"));   // 操作类型
        log.put("operateTime", System.currentTimeMillis());  // 操作时间
        log.put("loginTime", tokenInfo.getTimestamp());  // 登录时间
        log.put("sessionId", tokenInfo.getTimestamp() + "-" + tokenInfo.getUid());
        
        return Result.success("操作日志记录成功", log);
    }
}

