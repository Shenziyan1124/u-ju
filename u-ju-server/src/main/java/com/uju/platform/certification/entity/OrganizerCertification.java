package com.uju.platform.certification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("organizer_certifications")
public class OrganizerCertification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String companyName;
    private String creditCode;
    private String businessLicense;
    private String companyAddress;
    private String contactPerson;
    private String contactPhone;
    private Integer status;
    private String reviewRemark;
    private Long reviewerId;
    private LocalDateTime reviewedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
