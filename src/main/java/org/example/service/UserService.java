package org.example.service;

import org.example.dto.ChangePasswordRequest;
import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务层
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 用户信息（不包含密码）
     */
    public User login(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        // 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        String encryptedPassword = PasswordUtil.encrypt(request.getPassword());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        // 返回用户信息，清除密码
        user.setPassword(null);
        return user;
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册成功的用户信息
     */
    @Transactional
    public User register(RegisterRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
            throw new RuntimeException("用户名长度必须在3-20个字符之间");
        }
        if (request.getPassword().length() < 6) {
            throw new RuntimeException("密码长度不能少于6个字符");
        }

        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(request.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            User existEmail = userMapper.findByEmail(request.getEmail());
            if (existEmail != null) {
                throw new RuntimeException("邮箱已被使用");
            }
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encrypt(request.getPassword())); // SHA-256加密
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus(1); // 默认启用

        // 插入数据库
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new RuntimeException("注册失败");
        }

        // 返回用户信息，清除密码
        user.setPassword(null);
        return user;
    }

    /**
     * 修改密码
     *
     * @param request 修改密码请求
     * @return 是否成功
     */
    @Transactional
    public boolean changePassword(ChangePasswordRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getOldPassword() == null || request.getOldPassword().trim().isEmpty()) {
            throw new RuntimeException("旧密码不能为空");
        }
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        if (request.getNewPassword().length() < 6) {
            throw new RuntimeException("新密码长度不能少于6个字符");
        }

        // 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        String encryptedOldPassword = PasswordUtil.encrypt(request.getOldPassword());
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        String encryptedNewPassword = PasswordUtil.encrypt(request.getNewPassword());
        int result = userMapper.updatePassword(request.getUsername(), encryptedNewPassword);
        
        return result > 0;
    }

    /**
     * 根据用户名查询用户（不返回密码）
     */
    public User findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
}

