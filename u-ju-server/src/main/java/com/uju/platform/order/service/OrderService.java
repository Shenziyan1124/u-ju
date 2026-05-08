package com.uju.platform.order.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.BusinessException;
import com.uju.platform.event.entity.Ticket;
import com.uju.platform.event.mapper.TicketMapper;
import com.uju.platform.order.entity.Order;
import com.uju.platform.order.mapper.OrderMapper;
import com.uju.platform.registration.entity.Registration;
import com.uju.platform.registration.mapper.RegistrationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final RegistrationMapper registrationMapper;
    private final TicketMapper ticketMapper;

    @Transactional
    public Order createOrder(Long userId, Long registrationId, Long ticketId) {
        Registration registration = registrationMapper.selectById(registrationId);
        if (registration == null || !registration.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此报名");
        }

        BigDecimal amount = BigDecimal.ZERO;
        if (ticketId != null) {
            Ticket ticket = ticketMapper.selectById(ticketId);
            if (ticket != null) {
                amount = ticket.getPrice();
            }
        }

        Order order = new Order();
        order.setOrderNo("ORD" + IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setRegistrationId(registrationId);
        order.setEventId(registration.getEventId());
        order.setTicketId(ticketId);
        order.setAmount(amount);
        order.setStatus(0);
        order.setExpireAt(LocalDateTime.now().plusMinutes(15));
        orderMapper.insert(order);
        return order;
    }

    public Page<Order> getMyOrders(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        return orderMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreatedAt));
    }

    public Order getOrderDetail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    @Transactional
    public Order payOrder(Long userId, Long id, Integer payMethod) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "订单状态不允许支付");
        }
        if (order.getExpireAt().isBefore(LocalDateTime.now())) {
            order.setStatus(3);
            orderMapper.updateById(order);
            throw new BusinessException(400, "订单已过期");
        }

        order.setStatus(2);
        order.setPayMethod(payMethod);
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId("TXN" + IdUtil.getSnowflakeNextIdStr());
        orderMapper.updateById(order);
        return order;
    }

    public void cancelOrder(Long userId, Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        order.setStatus(3);
        orderMapper.updateById(order);
    }

    public void refundOrder(Long userId, Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(400, "订单状态不允许退款");
        }
        order.setStatus(5);
        orderMapper.updateById(order);
    }

    public Page<Order> getAllOrders(int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        return orderMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Order>().orderByDesc(Order::getCreatedAt));
    }

    @Transactional
    public void approveRefund(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 5) {
            throw new BusinessException(400, "订单状态不允许审核退款");
        }
        order.setStatus(4);
        orderMapper.updateById(order);
    }
}
