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
 * SysDict 实体类
 * 对应数据库表: sys_dict
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_dict")
public class SysDict {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String dictCode;

    private String dictName;

    private String description;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
