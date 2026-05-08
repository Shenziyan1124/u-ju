package com.uju.platform.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uju.platform.event.entity.Event;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventMapper extends BaseMapper<Event> {
}
