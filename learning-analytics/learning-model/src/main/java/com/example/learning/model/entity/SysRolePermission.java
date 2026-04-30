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
 * SysRolePermission 实体类
 * 对应数据库表: sys_role_permission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role_permission")
public class SysRolePermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long permissionId;

    private Long tenantId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
