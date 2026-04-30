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
 * SysPermission 实体类
 * 对应数据库表: sys_permission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_permission")
public class SysPermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String permissionCode;

    private String permissionName;

    private String resourceType;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
