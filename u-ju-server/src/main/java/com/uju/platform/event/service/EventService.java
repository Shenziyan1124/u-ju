package com.uju.platform.event.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uju.platform.common.BusinessException;
import com.uju.platform.event.entity.Event;
import com.uju.platform.event.entity.Ticket;
import com.uju.platform.event.mapper.EventMapper;
import com.uju.platform.event.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final TicketMapper ticketMapper;

    public Page<Event> listEvents(int page, int size, String category, String keyword, Integer isFree, String sort) {
        Page<Event> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<Event>()
                .eq(Event::getStatus, 1)
                .eq(Event::getIsPublic, 1);

        if (category != null && !category.isEmpty()) {
            wrapper.eq(Event::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Event::getTitle, keyword).or().like(Event::getSummary, keyword));
        }
        if (isFree != null) {
            wrapper.eq(Event::getIsFree, isFree);
        }

        if ("hot".equals(sort)) {
            wrapper.orderByDesc(Event::getViewCount);
        } else if ("price".equals(sort)) {
            wrapper.orderByAsc(Event::getIsFree);
        } else if ("starting".equals(sort)) {
            wrapper.ge(Event::getStartTime, java.time.LocalDateTime.now())
                   .orderByAsc(Event::getStartTime);
        } else {
            wrapper.orderByDesc(Event::getCreatedAt);
        }

        return eventMapper.selectPage(pageParam, wrapper);
    }

    public Event getEventDetail(Long id) {
        Event event = eventMapper.selectById(id);
        if (event == null) {
            throw new BusinessException(404, "活动不存在");
        }
        eventMapper.update(null, new LambdaUpdateWrapper<Event>()
                .eq(Event::getId, id)
                .setSql("view_count = view_count + 1"));
        return event;
    }

    @Transactional
    public Event createEvent(Long publisherId, Event event) {
        event.setPublisherId(publisherId);
        event.setViewCount(0);
        eventMapper.insert(event);
        return event;
    }

    @Transactional
    public Event updateEvent(Long publisherId, Long eventId, Event eventUpdate) {
        Event event = eventMapper.selectById(eventId);
        if (event == null || !event.getPublisherId().equals(publisherId)) {
            throw new BusinessException(403, "无权修改此活动");
        }
        eventUpdate.setId(eventId);
        eventUpdate.setPublisherId(publisherId);
        eventMapper.updateById(eventUpdate);
        return eventMapper.selectById(eventId);
    }

    public void updateEventStatus(Long publisherId, Long eventId, Integer status) {
        Event event = eventMapper.selectById(eventId);
        if (event == null || !event.getPublisherId().equals(publisherId)) {
            throw new BusinessException(403, "无权操作此活动");
        }
        event.setStatus(status);
        eventMapper.updateById(event);
    }

    public Page<Event> getMyEvents(Long publisherId, int page, int size) {
        Page<Event> pageParam = new Page<>(page, size);
        return eventMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Event>()
                        .eq(Event::getPublisherId, publisherId)
                        .orderByDesc(Event::getCreatedAt));
    }

    @Transactional
    public Ticket createTicket(Ticket ticket) {
        ticket.setSoldQuantity(0);
        ticketMapper.insert(ticket);
        return ticket;
    }

    public Page<Ticket> getEventTickets(Long eventId) {
        Page<Ticket> page = new Page<>(1, 100);
        return ticketMapper.selectPage(page,
                new LambdaQueryWrapper<Ticket>().eq(Ticket::getEventId, eventId));
    }
}
