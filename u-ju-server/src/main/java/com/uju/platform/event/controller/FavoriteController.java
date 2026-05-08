package com.uju.platform.event.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.Result;
import com.uju.platform.event.entity.Event;
import com.uju.platform.event.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "收藏模块")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "切换收藏状态")
    @PostMapping("/{eventId}/favorite")
    public Result<Map<String, Object>> toggle(Authentication authentication, @PathVariable Long eventId) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(favoriteService.toggleFavorite(userId, eventId));
    }

    @Operation(summary = "收藏列表")
    @GetMapping("/favorites")
    public Result<Page<Event>> list(Authentication authentication,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(favoriteService.getFavoriteEvents(userId, page, size));
    }
}
