package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.SysMessage;
import com.example.learning.model.mapper.SysMessageMapper;
import com.example.learning.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统消息服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private SysMessageMapper sysMessageMapper;

    @Override
    public Page<SysMessage> getPage(Long receiverId, String messageType, Integer isRead, int page, int size) {
        Page<SysMessage> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        if (receiverId != null) {
            wrapper.eq(SysMessage::getReceiverId, receiverId);
        }
        if (StringUtils.hasText(messageType)) {
            wrapper.eq(SysMessage::getMessageType, messageType);
        }
        if (isRead != null) {
            wrapper.eq(SysMessage::getIsRead, isRead);
        }
        wrapper.eq(SysMessage::getStatus, 1);
        wrapper.orderByDesc(SysMessage::getCreatedAt);
        return sysMessageMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(SysMessage message) {
        message.setIsRead(0);
        message.setStatus(1);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        sysMessageMapper.insert(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id) {
        SysMessage existing = sysMessageMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        SysMessage update = new SysMessage();
        update.setId(id);
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());
        update.setUpdatedAt(LocalDateTime.now());
        sysMessageMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long receiverId) {
        LambdaUpdateWrapper<SysMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysMessage::getReceiverId, receiverId)
               .eq(SysMessage::getIsRead, 0)
               .eq(SysMessage::getStatus, 1)
               .set(SysMessage::getIsRead, 1)
               .set(SysMessage::getReadTime, LocalDateTime.now())
               .set(SysMessage::getUpdatedAt, LocalDateTime.now());
        sysMessageMapper.update(null, wrapper);
    }

    @Override
    public int getUnreadCount(Long receiverId) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getReceiverId, receiverId)
               .eq(SysMessage::getIsRead, 0)
               .eq(SysMessage::getStatus, 1);
        return Math.toIntExact(sysMessageMapper.selectCount(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysMessage existing = sysMessageMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 逻辑删除
        SysMessage update = new SysMessage();
        update.setId(id);
        update.setStatus(0);
        update.setUpdatedAt(LocalDateTime.now());
        sysMessageMapper.updateById(update);
    }
}
