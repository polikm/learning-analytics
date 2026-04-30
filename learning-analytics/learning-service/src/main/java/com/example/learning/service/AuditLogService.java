package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.entity.SysAuditLog;

/**
 * 审计日志服务接口
 */
public interface AuditLogService {

    /**
     * 分页查询审计日志
     *
     * @param userId    操作用户ID
     * @param operation 操作描述关键词
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param page      当前页码
     * @param size      每页大小
     * @return 分页结果
     */
    Page<SysAuditLog> getPage(Long userId, String operation, String startTime, String endTime,
                              int page, int size);

    /**
     * 获取审计日志详情
     *
     * @param id 日志ID
     * @return 审计日志
     */
    SysAuditLog getById(Long id);

    /**
     * 记录审计日志
     *
     * @param log 审计日志实体
     */
    void record(SysAuditLog log);
}
