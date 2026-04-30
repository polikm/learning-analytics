package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.SysAuditLog;
import com.example.learning.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 审计日志Controller
 */
@Tag(name = "审计日志")
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * 分页查询审计日志
     *
     * @param userId    操作用户ID
     * @param operation 操作描述关键词
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param page      当前页码
     * @param pageSize  每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询审计日志")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<SysAuditLog>> getPage(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Page<SysAuditLog> pageResult = auditLogService.getPage(
                userId, operation, startTime, endTime, page, pageSize);
        PageResult<SysAuditLog> result = PageResult.of(
                pageResult.getRecords(),
                pageResult.getTotal(),
                pageResult.getCurrent(),
                pageResult.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取审计日志详情
     *
     * @param id 日志ID
     * @return 审计日志详情
     */
    @Operation(summary = "审计日志详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<SysAuditLog> getById(@PathVariable Long id) {
        SysAuditLog log = auditLogService.getById(id);
        return R.ok(log);
    }
}
