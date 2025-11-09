package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.annotation.GuestAllowed;
import org.example.annotation.RequireAuth;
import org.example.common.Result;
import org.example.dto.ChangePasswordRequest;
import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
import org.example.dto.RegisterRequest;
import org.example.entity.User;
import org.example.service.TokenService;
import org.example.service.UserService;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 用户登录（返回JWT Token）
     */
    @PostMapping("/login")
    @GuestAllowed
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            // 验证用户名密码
            User user = userService.login(request);
            
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            
            // 将 Token 保存到 Redis
            tokenService.saveToken(user.getUsername(), token);
            
            // 构建响应
            LoginResponse response = new LoginResponse(token, user, expiration);
            
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @GuestAllowed
    public Result<User> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出（使 Token 失效）
     */
    @PostMapping("/logout")
    @RequireAuth
    public Result<String> logout(HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            
            // 从 Redis 中删除 Token
            tokenService.deleteToken(username);
            
            return Result.success("登出成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh-token")
    @RequireAuth
    public Result<Map<String, Object>> refreshToken(HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            Long userId = (Long) request.getAttribute("userId");
            
            // 获取当前 Token
            String oldToken = getTokenFromRequest(request);
            
            // 生成新 Token
            String newToken = jwtUtil.generateToken(userId, username);
            
            // 刷新 Redis 中的 Token
            tokenService.refreshToken(username, newToken);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);
            result.put("expiresIn", expiration);
            
            return Result.success("Token 刷新成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @RequireAuth
    public Result<Boolean> changePassword(@RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest) {
        try {
            String currentUsername = (String) httpRequest.getAttribute("username");
            
            // 确保用户只能修改自己的密码
            if (!currentUsername.equals(request.getUsername())) {
                return Result.error("只能修改自己的密码");
            }
            
            boolean success = userService.changePassword(request);
            if (success) {
                // 修改密码后，使当前 Token 失效
                tokenService.deleteToken(currentUsername);
                return Result.success("密码修改成功，请重新登录", true);
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据用户名查询用户信息（游客也可以访问）
     */
    @GetMapping("/{username}")
    @GuestAllowed
    public Result<User> getUserInfo(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    @RequireAuth
    public Result<User> getCurrentUser(HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查 Token 是否有效
     */
    @GetMapping("/check-token")
    @RequireAuth
    public Result<Map<String, Object>> checkToken(HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            Long lastActivity = tokenService.getLastActivity(username);
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("username", username);
            result.put("lastActivity", lastActivity);
            
            return Result.success("Token 有效", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从请求头中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

