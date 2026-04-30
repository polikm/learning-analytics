package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.entity.SysAuditLog;
import com.example.learning.model.mapper.SysAuditLogMapper;
import com.example.learning.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 审计日志服务实现类
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private SysAuditLogMapper sysAuditLogMapper;

    @Override
    public Page<SysAuditLog> getPage(Long userId, String operation, String startTime, String endTime,
                                     int page, int size) {
        LambdaQueryWrapper<SysAuditLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(SysAuditLog::getUserId, userId);
        }
        if (operation != null && !operation.isEmpty()) {
            wrapper.like(SysAuditLog::getOperation, operation);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(SysAuditLog::getCreatedAt, LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(SysAuditLog::getCreatedAt, LocalDateTime.parse(endTime));
        }
        wrapper.orderByDesc(SysAuditLog::getCreatedAt);
        return sysAuditLogMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public SysAuditLog getById(Long id) {
        return sysAuditLogMapper.selectById(id);
    }

    @Override
    public void record(SysAuditLog log) {
        if (log.getCreatedAt() == null) {
            log.setCreatedAt(LocalDateTime.now());
        }
        if (log.getUpdatedAt() == null) {
            log.setUpdatedAt(LocalDateTime.now());
        }
        sysAuditLogMapper.insert(log);
    }
}
