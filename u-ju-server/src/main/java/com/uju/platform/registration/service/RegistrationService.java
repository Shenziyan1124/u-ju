package com.uju.platform.registration.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.BusinessException;
import com.uju.platform.event.entity.Event;
import com.uju.platform.event.mapper.EventMapper;
import com.uju.platform.registration.entity.Registration;
import com.uju.platform.registration.mapper.RegistrationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final EventMapper eventMapper;

    public Registration register(Long userId, Registration registration) {
        Event event = eventMapper.selectById(registration.getEventId());
        if (event == null) {
            throw new BusinessException(404, "活动不存在");
        }
        if (event.getStatus() != 1) {
            throw new BusinessException(400, "活动未开放报名");
        }

        Long count = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getUserId, userId)
                        .eq(Registration::getEventId, registration.getEventId())
                        .ne(Registration::getStatus, 3)
        );
        if (count > 0) {
            throw new BusinessException(400, "您已报名此活动");
        }

        registration.setUserId(userId);
        if (event.getNeedReview() == 1) {
            registration.setStatus(0);
        } else {
            registration.setStatus(1);
        }
        registration.setTicketCode("EVT" + IdUtil.getSnowflakeNextIdStr());
        registrationMapper.insert(registration);
        return registration;
    }

    public Page<Registration> getMyRegistrations(Long userId, int page, int size) {
        Page<Registration> pageParam = new Page<>(page, size);
        return registrationMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getUserId, userId)
                        .orderByDesc(Registration::getCreatedAt));
    }

    public Registration getRegistrationDetail(Long id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            throw new BusinessException(404, "报名记录不存在");
        }
        return reg;
    }

    public void cancelRegistration(Long userId, Long id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null || !reg.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此报名");
        }
        reg.setStatus(3);
        registrationMapper.updateById(reg);
    }

    public Page<Registration> getEventRegistrations(Long eventId, int page, int size) {
        Page<Registration> pageParam = new Page<>(page, size);
        return registrationMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getEventId, eventId)
                        .orderByDesc(Registration::getCreatedAt));
    }

    public void reviewRegistration(Long id, Integer status, String remark) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            throw new BusinessException(404, "报名记录不存在");
        }
        reg.setStatus(status);
        registrationMapper.updateById(reg);
    }
}
