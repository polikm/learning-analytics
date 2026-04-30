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
 * TeacherAssignment 实体类
 * 对应数据库表: teacher_assignment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("teacher_assignment")
public class TeacherAssignment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long userId;

    private Long schoolId;

    private Long subjectId;

    private String classIds;

    private String academicYear;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
