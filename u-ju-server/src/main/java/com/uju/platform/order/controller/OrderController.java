package com.uju.platform.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.Result;
import com.uju.platform.order.entity.Order;
import com.uju.platform.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单模块")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> create(Authentication authentication,
                                @RequestParam Long registrationId,
                                @RequestParam(required = false) Long ticketId) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.createOrder(userId, registrationId, ticketId));
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/my")
    public Result<Page<Order>> myOrders(Authentication authentication,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.getMyOrders(userId, page, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{id}/pay")
    public Result<Order> pay(Authentication authentication, @PathVariable Long id, @RequestParam Integer payMethod) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(orderService.payOrder(userId, id, payMethod));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    @Operation(summary = "申请退款")
    @PostMapping("/{id}/refund")
    public Result<Void> refund(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        orderService.refundOrder(userId, id);
        return Result.success();
    }
}
