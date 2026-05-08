package com.uju.platform.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.certification.entity.OrganizerCertification;
import com.uju.platform.certification.service.CertificationService;
import com.uju.platform.common.Result;
import com.uju.platform.event.entity.Event;
import com.uju.platform.order.entity.Order;
import com.uju.platform.order.service.OrderService;
import com.uju.platform.admin.service.AdminService;
import com.uju.platform.registration.entity.Registration;
import com.uju.platform.registration.service.RegistrationService;
import com.uju.platform.user.dto.LoginResponse;
import com.uju.platform.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理模块")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CertificationService certificationService;
    private final OrderService orderService;
    private final RegistrationService registrationService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestParam String phone, @RequestParam String password) {
        return Result.success(adminService.login(phone, password));
    }

    @Operation(summary = "数据概览")
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(adminService.getDashboard());
    }

    @Operation(summary = "用户列表")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> users(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String keyword) {
        return Result.success(adminService.getUsers(page, size, keyword));
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "认证列表")
    @GetMapping("/certifications")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<OrganizerCertification>> certifications(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(required = false) Integer status) {
        return Result.success(adminService.getCertifications(page, size, status));
    }

    @Operation(summary = "审核认证")
    @PutMapping("/certifications/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> reviewCertification(Authentication authentication,
                                             @PathVariable Long id,
                                             @RequestParam Integer status,
                                             @RequestParam(required = false) String remark) {
        Long reviewerId = (Long) authentication.getPrincipal();
        certificationService.reviewCertification(id, status, remark, reviewerId);
        return Result.success();
    }

    @Operation(summary = "活动列表")
    @GetMapping("/events")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Event>> events(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) Integer status) {
        return Result.success(adminService.getEvents(page, size, status));
    }

    @Operation(summary = "审核活动")
    @PutMapping("/events/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> reviewEvent(@PathVariable Long id, @RequestParam Integer status) {
        adminService.reviewEvent(id, status);
        return Result.success();
    }

    @Operation(summary = "订单列表")
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Order>> orders(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) Integer status) {
        return Result.success(adminService.getOrders(page, size, status));
    }

    @Operation(summary = "审核退款")
    @PutMapping("/orders/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> approveRefund(@PathVariable Long id) {
        orderService.approveRefund(id);
        return Result.success();
    }

    @Operation(summary = "报名列表")
    @GetMapping("/registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Registration>> registrations(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) Integer status) {
        return Result.success(adminService.getRegistrations(page, size, status));
    }

    @Operation(summary = "审核报名")
    @PutMapping("/registrations/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> reviewRegistration(@PathVariable Long id,
                                            @RequestParam Integer status,
                                            @RequestParam(required = false) String remark) {
        registrationService.reviewRegistration(id, status, remark);
        return Result.success();
    }
}
