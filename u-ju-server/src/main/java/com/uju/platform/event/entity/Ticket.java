package com.uju.platform.event.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tickets")
public class Ticket {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long eventId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Integer soldQuantity;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
