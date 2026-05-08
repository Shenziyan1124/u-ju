package com.uju.platform.event.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("events")
public class Event {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long publisherId;
    private String title;
    private String coverImages;
    private String category;
    private String tags;
    private String summary;
    private String detail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registrationDeadline;
    private Integer locationType;
    private String province;
    private String city;
    private String district;
    private String address;
    private Integer isFree;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Integer needReview;
    private Integer status;
    private Integer isPublic;
    private String contact;
    private String refundPolicy;
    private Integer isRecommended;
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
