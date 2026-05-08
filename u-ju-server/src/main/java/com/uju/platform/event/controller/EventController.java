package com.uju.platform.event.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.Result;
import com.uju.platform.event.entity.Event;
import com.uju.platform.event.entity.Ticket;
import com.uju.platform.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "活动模块")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "活动列表")
    @GetMapping
    public Result<Page<Event>> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Integer isFree,
                                    @RequestParam(defaultValue = "latest") String sort) {
        return Result.success(eventService.listEvents(page, size, category, keyword, isFree, sort));
    }

    @Operation(summary = "活动详情")
    @GetMapping("/{id}")
    public Result<Event> detail(@PathVariable Long id) {
        return Result.success(eventService.getEventDetail(id));
    }

    @Operation(summary = "发布活动")
    @PostMapping
    public Result<Event> create(Authentication authentication, @RequestBody Event event) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(eventService.createEvent(userId, event));
    }

    @Operation(summary = "编辑活动")
    @PutMapping("/{id}")
    public Result<Event> update(Authentication authentication, @PathVariable Long id, @RequestBody Event event) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(eventService.updateEvent(userId, id, event));
    }

    @Operation(summary = "修改活动状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(Authentication authentication, @PathVariable Long id, @RequestParam Integer status) {
        Long userId = (Long) authentication.getPrincipal();
        eventService.updateEventStatus(userId, id, status);
        return Result.success();
    }

    @Operation(summary = "我发布的活动")
    @GetMapping("/my")
    public Result<Page<Event>> myEvents(Authentication authentication,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(eventService.getMyEvents(userId, page, size));
    }

    @Operation(summary = "创建票型")
    @PostMapping("/{eventId}/tickets")
    public Result<Ticket> createTicket(@PathVariable Long eventId, @RequestBody Ticket ticket) {
        ticket.setEventId(eventId);
        return Result.success(eventService.createTicket(ticket));
    }

    @Operation(summary = "获取活动票型")
    @GetMapping("/{eventId}/tickets")
    public Result<Page<Ticket>> getEventTickets(@PathVariable Long eventId) {
        return Result.success(eventService.getEventTickets(eventId));
    }
}
