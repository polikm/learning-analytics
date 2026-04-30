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
 * SysRole 实体类
 * 对应数据库表: sys_role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String roleCode;

    private String roleName;

    private String description;

    @TableField("is_system")
    private Integer isSystem;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
