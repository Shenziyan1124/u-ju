package com.uju.platform.registration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.Result;
import com.uju.platform.registration.entity.Registration;
import com.uju.platform.registration.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报名模块")
@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "提交报名")
    @PostMapping
    public Result<Registration> register(Authentication authentication, @RequestBody Registration registration) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(registrationService.register(userId, registration));
    }

    @Operation(summary = "我的报名列表")
    @GetMapping("/my")
    public Result<Page<Registration>> myRegistrations(Authentication authentication,
                                                       @RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(registrationService.getMyRegistrations(userId, page, size));
    }

    @Operation(summary = "报名详情")
    @GetMapping("/{id}")
    public Result<Registration> detail(@PathVariable Long id) {
        return Result.success(registrationService.getRegistrationDetail(id));
    }

    @Operation(summary = "取消报名")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        registrationService.cancelRegistration(userId, id);
        return Result.success();
    }

    @Operation(summary = "活动报名列表")
    @GetMapping("/event/{eventId}")
    public Result<Page<Registration>> eventRegistrations(@PathVariable Long eventId,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        return Result.success(registrationService.getEventRegistrations(eventId, page, size));
    }

    @Operation(summary = "审核报名")
    @PutMapping("/{id}/review")
    public Result<Void> review(@PathVariable Long id, @RequestParam Integer status, @RequestParam(required = false) String remark) {
        registrationService.reviewRegistration(id, status, remark);
        return Result.success();
    }
}
