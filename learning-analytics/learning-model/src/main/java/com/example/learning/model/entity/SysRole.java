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

    private String roleName;

    private String roleCode;

    /** 数据范围: 1-全部, 2-本学校, 3-本年级, 4-本班级, 5-本人 */
    private Integer dataScope;

    private Integer sortOrder;

    /** 状态: 1-启用, 0-禁用 */
    private Integer status;

    private String remark;

    private Long tenantId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
