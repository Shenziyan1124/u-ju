package com.uju.platform.config;

import com.uju.platform.user.entity.User;
import com.uju.platform.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User admin = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getRole, "ADMIN"));
        
        if (admin != null) {
            admin.setPassword(passwordEncoder.encode("admin"));
            userMapper.updateById(admin);
            System.out.println("Admin password reset to 'admin'");
        }
    }
}
