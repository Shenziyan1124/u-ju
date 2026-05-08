package com.uju.platform.order.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.uju.platform.order.entity.Order;
import com.uju.platform.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderMapper orderMapper;

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredOrders() {
        try {
            List<Order> expiredOrders = orderMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                            .eq(Order::getStatus, 0)
                            .lt(Order::getExpireAt, LocalDateTime.now())
            );
            if (!expiredOrders.isEmpty()) {
                for (Order order : expiredOrders) {
                    order.setStatus(3);
                    orderMapper.updateById(order);
                }
                log.info("自动取消超时订单: {} 笔", expiredOrders.size());
            }
        } catch (Exception e) {
            log.error("自动取消订单任务异常", e);
        }
    }
}
