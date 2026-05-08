package com.uju.platform.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.certification.entity.OrganizerCertification;
import com.uju.platform.certification.mapper.CertificationMapper;
import com.uju.platform.common.BusinessException;
import com.uju.platform.event.entity.Event;
import com.uju.platform.event.mapper.EventMapper;
import com.uju.platform.order.entity.Order;
import com.uju.platform.order.mapper.OrderMapper;
import com.uju.platform.registration.entity.Registration;
import com.uju.platform.registration.mapper.RegistrationMapper;
import com.uju.platform.user.dto.LoginResponse;
import com.uju.platform.user.dto.UserVO;
import com.uju.platform.user.entity.User;
import com.uju.platform.user.mapper.UserMapper;
import com.uju.platform.util.JwtUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;
    private final CertificationMapper certificationMapper;
    private final OrderMapper orderMapper;
    private final RegistrationMapper registrationMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(String phone, String password) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "手机号或密码错误");
        }
        if (!"ADMIN".equals(user.getRole())) {
            throw new BusinessException(403, "非管理员账号");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return new LoginResponse(token, userVO);
    }

    public Map<String, Object> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("eventCount", eventMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("orderCount", orderMapper.selectCount(new LambdaQueryWrapper<>()));
        data.put("certificationCount", certificationMapper.selectCount(
                new LambdaQueryWrapper<OrganizerCertification>().eq(OrganizerCertification::getStatus, 0)));
        return data;
    }

    public Page<User> getUsers(int page, int size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getPhone, keyword)
                    .or().like(User::getRealName, keyword));
        }
        wrapper.orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(pageParam, wrapper);
    }

    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
    }

    public Page<OrganizerCertification> getCertifications(int page, int size, Integer status) {
        Page<OrganizerCertification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OrganizerCertification> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(OrganizerCertification::getStatus, status);
        wrapper.orderByDesc(OrganizerCertification::getCreatedAt);
        return certificationMapper.selectPage(pageParam, wrapper);
    }

    public Page<Event> getEvents(int page, int size, Integer status) {
        Page<Event> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Event::getStatus, status);
        wrapper.orderByDesc(Event::getCreatedAt);
        return eventMapper.selectPage(pageParam, wrapper);
    }

    public void reviewEvent(Long eventId, Integer status) {
        Event event = eventMapper.selectById(eventId);
        if (event == null) throw new BusinessException(404, "活动不存在");
        event.setStatus(status);
        eventMapper.updateById(event);
    }

    public Page<Order> getOrders(int page, int size, Integer status) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Order::getStatus, status);
        wrapper.orderByDesc(Order::getCreatedAt);
        return orderMapper.selectPage(pageParam, wrapper);
    }

    public Page<Registration> getRegistrations(int page, int size, Integer status) {
        Page<Registration> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Registration::getStatus, status);
        wrapper.orderByDesc(Registration::getCreatedAt);
        return registrationMapper.selectPage(pageParam, wrapper);
    }
}
