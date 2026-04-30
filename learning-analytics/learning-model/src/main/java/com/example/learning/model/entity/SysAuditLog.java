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

    /** 操作用户ID */
    private Long userId;

    /** 操作用户名 */
    private String username;

    /** 操作描述 */
    private String operation;

    /** 请求方法(类名.方法名) */
    private String method;

    /** 请求URL */
    private String requestUrl;

    /** HTTP方法(GET/POST/PUT/DELETE) */
    private String requestMethod;

    /** 请求参数 */
    private String requestParams;

    /** 响应数据 */
    private String responseData;

    /** IP地址 */
    private String ipAddress;

    /** 用户代理 */
    private String userAgent;

    /** 执行时长(毫秒) */
    private Integer duration;

    /** 状态: 1-成功, 0-失败 */
    private Integer status;

    /** 错误信息 */
    private String errorMsg;

    /** 租户ID */
    private Long tenantId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
