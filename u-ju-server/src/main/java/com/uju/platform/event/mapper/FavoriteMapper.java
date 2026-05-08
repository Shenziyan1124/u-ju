package com.uju.platform.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uju.platform.event.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
