package org.example.controller;

import org.example.common.Result;
import org.example.dto.ChangePasswordRequest;
import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            return Result.success("登录成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Boolean> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            boolean success = userService.changePassword(request);
            if (success) {
                return Result.success("密码修改成功", true);
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/{username}")
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
}

