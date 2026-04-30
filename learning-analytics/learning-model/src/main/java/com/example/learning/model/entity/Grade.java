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
 * Grade 实体类
 * 对应数据库表: grade
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("grade")
public class Grade {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long schoolId;

    private String gradeName;

    private String gradeLevel;

    private String academicYear;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
