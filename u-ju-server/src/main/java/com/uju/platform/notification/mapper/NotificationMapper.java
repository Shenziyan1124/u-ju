package com.uju.platform.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uju.platform.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
