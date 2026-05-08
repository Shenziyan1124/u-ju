package com.uju.platform.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uju.platform.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.redis.core.StringRedisTemplate;

@Tag(name = "内容管理模块")
@RestController
@RequestMapping("/api/admin/content")
@RequiredArgsConstructor
public class ContentController {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/banners")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Map<String, Object>>> banners() {
        List<String> rawList = redisTemplate.opsForList().range("content:banners", 0, -1);
        if (rawList == null || rawList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            for (String json : rawList) {
                result.add(objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {}));
            }
            return Result.success(result);
        } catch (JsonProcessingException e) {
            return Result.success(new ArrayList<>());
        }
    }

    @Operation(summary = "保存轮播图")
    @PostMapping("/banners")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> saveBanners(@RequestBody List<Map<String, Object>> banners) throws JsonProcessingException {
        redisTemplate.delete("content:banners");
        if (!banners.isEmpty()) {
            List<String> jsonList = new ArrayList<>();
            for (Map<String, Object> banner : banners) {
                jsonList.add(objectMapper.writeValueAsString(banner));
            }
            redisTemplate.opsForList().rightPushAll("content:banners", jsonList);
        }
        return Result.success();
    }

    @Operation(summary = "获取公告列表")
    @GetMapping("/announcements")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Map<String, Object>>> announcements() {
        List<String> rawList = redisTemplate.opsForList().range("content:announcements", 0, -1);
        if (rawList == null || rawList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            for (String json : rawList) {
                result.add(objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {}));
            }
            return Result.success(result);
        } catch (JsonProcessingException e) {
            return Result.success(new ArrayList<>());
        }
    }

    @Operation(summary = "保存公告")
    @PostMapping("/announcements")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> saveAnnouncements(@RequestBody List<Map<String, Object>> announcements) throws JsonProcessingException {
        redisTemplate.delete("content:announcements");
        if (!announcements.isEmpty()) {
            List<String> jsonList = new ArrayList<>();
            for (Map<String, Object> item : announcements) {
                jsonList.add(objectMapper.writeValueAsString(item));
            }
            redisTemplate.opsForList().rightPushAll("content:announcements", jsonList);
        }
        return Result.success();
    }

    @Operation(summary = "获取系统配置")
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, String>> config() {
        Map<String, String> config = new HashMap<>();
        config.put("appName", redisTemplate.opsForValue().get("config:app_name"));
        config.put("contactEmail", redisTemplate.opsForValue().get("config:contact_email"));
        config.put("wechatAppId", redisTemplate.opsForValue().get("config:wechat_appid"));
        config.put("smsSignName", redisTemplate.opsForValue().get("config:sms_sign_name"));
        return Result.success(config);
    }

    @Operation(summary = "更新系统配置")
    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateConfig(@RequestBody Map<String, String> config) {
        config.forEach((k, v) -> {
            if (v != null && !v.isEmpty()) {
                redisTemplate.opsForValue().set("config:" + k, v);
            }
        });
        return Result.success();
    }

    @Operation(summary = "获取平台统计数据（供前端Dashboard使用）")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", redisTemplate.opsForValue().get("stats:total_users"));
        stats.put("totalEvents", redisTemplate.opsForValue().get("stats:total_events"));
        stats.put("totalOrders", redisTemplate.opsForValue().get("stats:total_orders"));
        stats.put("todayRegistrations", redisTemplate.opsForValue().get("stats:today_registrations"));
        return Result.success(stats);
    }
}
