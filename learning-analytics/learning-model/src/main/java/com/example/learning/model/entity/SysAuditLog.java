package com.example.learning.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SysAuditLog 实体类
 * 对应数据库表: sys_audit_log
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_audit_log")
public class SysAuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long userId;

    private String userName;

    private String operation;

    private String resourceType;

    private String resourceId;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private Integer responseCode;

    private String ipAddress;

    private String userAgent;

    private Long duration;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
