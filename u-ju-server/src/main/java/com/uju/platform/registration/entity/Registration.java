package com.uju.platform.registration.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("registrations")
public class Registration {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long eventId;
    private Long ticketId;
    private String formData;
    private Integer status;
    private String ticketCode;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
