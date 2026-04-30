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
 * Tenant 实体类
 * 对应数据库表: tenant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tenant")
public class Tenant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tenantCode;

    private String tenantName;

    private String contactName;

    private String contactPhone;

    private Integer licenseCount;

    private LocalDateTime expireDate;

    private Integer status;

    @TableField("config")
    private String config;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
