package com.uju.platform.user.controller;

import com.uju.platform.common.Result;
import com.uju.platform.user.dto.*;
import com.uju.platform.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/send")
    public Result<Void> sendSmsCode(@RequestParam String phone) {
        String key = "sms:code:" + phone;
        String existing = redisTemplate.opsForValue().get(key);
        if (existing != null) {
            throw new com.uju.platform.common.BusinessException(429, "验证码已发送，请60秒后重试");
        }
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        redisTemplate.opsForValue().set(key, code, java.time.Duration.ofMinutes(5));
        return Result.success();
    }

    @Operation(summary = "验证码登录/注册")
    @PostMapping("/sms/login")
    public Result<LoginResponse> smsLogin(@RequestParam String phone, @RequestParam String code) {
        String key = "sms:code:" + phone;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new com.uju.platform.common.BusinessException(400, "验证码无效或已过期");
        }
        redisTemplate.delete(key);
        return Result.success(userService.loginOrRegisterBySms(phone));
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @Operation(summary = "密码登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(userService.getCurrentUser(userId));
    }

    @Operation(summary = "更新个人资料")
    @PutMapping("/me")
    public Result<UserVO> updateProfile(Authentication authentication, @RequestBody UserVO vo) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(userService.updateProfile(userId, vo));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(Authentication authentication,
                                       @RequestParam String oldPassword,
                                       @RequestParam String newPassword) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<Void> updateAvatar(Authentication authentication, @RequestParam String avatarUrl) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updateAvatar(userId, avatarUrl);
        return Result.success();
    }
}
