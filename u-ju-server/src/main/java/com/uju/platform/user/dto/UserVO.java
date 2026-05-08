package com.uju.platform.user.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String phone;
    private String realName;
    private String avatar;
    private String email;
    private Integer gender;
    private LocalDate birthday;
    private String role;
    private Integer status;
    private LocalDateTime createdAt;
}
