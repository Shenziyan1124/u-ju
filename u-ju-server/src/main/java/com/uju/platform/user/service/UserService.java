package com.uju.platform.user.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uju.platform.common.BusinessException;
import com.uju.platform.user.dto.*;
import com.uju.platform.user.entity.User;
import com.uju.platform.user.mapper.UserMapper;
import com.uju.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse register(RegisterRequest request) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone())
        );
        if (count > 0) {
            throw new BusinessException(400, "该手机号已注册");
        }

        count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(400, "该用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail() != null ? request.getEmail() : "");
        user.setGender(request.getGender() != null ? request.getGender() : 0);
        user.setRole("PARTICIPANT");
        user.setStatus(1);
        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return new LoginResponse(token, toVO(user));
    }

    public LoginResponse loginOrRegisterBySms(String phone) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone)
        );
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setUsername("user_" + phone.substring(phone.length() - 4));
            user.setPassword(passwordEncoder.encode(String.valueOf((int)(Math.random() * 900000) + 100000)));
            user.setRole("PARTICIPANT");
            user.setStatus(1);
            userMapper.insert(user);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return new LoginResponse(token, toVO(user));
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone())
        );
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "手机号或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return new LoginResponse(token, toVO(user));
    }

    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return toVO(user);
    }

    public UserVO updateProfile(Long userId, UserVO vo) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (vo.getRealName() != null) user.setRealName(vo.getRealName());
        if (vo.getEmail() != null) user.setEmail(vo.getEmail());
        if (vo.getGender() != null) user.setGender(vo.getGender());
        if (vo.getBirthday() != null) user.setBirthday(vo.getBirthday());
        userMapper.updateById(user);
        return toVO(user);
    }

    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    public void updateAvatar(Long userId, String avatarUrl) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}
