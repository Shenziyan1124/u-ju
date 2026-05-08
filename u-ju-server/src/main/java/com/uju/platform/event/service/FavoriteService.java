package com.uju.platform.event.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.BusinessException;
import com.uju.platform.event.entity.Event;
import com.uju.platform.event.entity.Favorite;
import com.uju.platform.event.mapper.EventMapper;
import com.uju.platform.event.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final EventMapper eventMapper;

    public Map<String, Object> toggleFavorite(Long userId, Long eventId) {
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new BusinessException(404, "活动不存在");
        }

        Favorite existing = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getEventId, eventId));

        boolean favorited;
        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            favorited = false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setEventId(eventId);
            favoriteMapper.insert(favorite);
            favorited = true;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("favorited", favorited);
        return result;
    }

    public Page<Event> getFavoriteEvents(Long userId, int page, int size) {
        Page<Favorite> favoritePage = new Page<>(page, size);
        Page<Favorite> favorites = favoriteMapper.selectPage(favoritePage,
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt));

        Page<Event> result = new Page<>(favorites.getCurrent(), favorites.getSize(), favorites.getTotal());
        result.setRecords(favorites.getRecords().stream()
                .map(f -> eventMapper.selectById(f.getEventId()))
                .filter(e -> e != null)
                .toList());
        return result;
    }
}
