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
 * Class 实体类
 * 对应数据库表: class
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("class")
public class Class {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long schoolId;

    private Long gradeId;

    private String className;

    private String classNo;

    private Long advisorId;

    private Integer maxStudents;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
