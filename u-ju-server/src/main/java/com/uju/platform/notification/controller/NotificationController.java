package com.uju.platform.notification.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.Result;
import com.uju.platform.notification.entity.Notification;
import com.uju.platform.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通知模块")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "通知列表")
    @GetMapping
    public Result<Page<Notification>> list(Authentication authentication,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(notificationService.getNotifications(userId, page, size));
    }

    @Operation(summary = "未读数量")
    @GetMapping("/unread-count")
    public Result<Long> unreadCount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    @Operation(summary = "全部已读")
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
}
