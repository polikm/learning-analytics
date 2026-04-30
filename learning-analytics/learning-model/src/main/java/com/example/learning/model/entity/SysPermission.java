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

    private String permissionName;

    private String permissionCode;

    /** 类型: 1-菜单, 2-按钮, 3-接口 */
    private Integer permissionType;

    private String path;

    private String component;

    private String icon;

    private Integer sortOrder;

    /** 是否可见: 1-是, 0-否 */
    private Integer visible;

    /** 状态: 1-启用, 0-禁用 */
    private Integer status;

    private String remark;

    private Long tenantId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
