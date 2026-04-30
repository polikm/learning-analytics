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
 * SysSetting 实体类
 * 对应数据库表: sys_setting
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_setting")
public class SysSetting {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String settingKey;

    private String settingValue;

    private String settingGroup;

    private String description;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
