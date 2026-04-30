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
 * School 实体类
 * 对应数据库表: school
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("school")
public class School {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String schoolCode;

    private String schoolName;

    private String schoolType;

    private String province;

    private String city;

    private String district;

    private String address;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
