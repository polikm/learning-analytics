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
 * StudentProfile 实体类
 * 对应数据库表: student_profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("student_profile")
public class StudentProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long tenantId;

    private Long schoolId;

    private Long gradeId;

    private Long classId;

    private String studentNo;

    private Integer gender;

    private LocalDateTime birthDate;

    private LocalDateTime enrollmentDate;

    private String academicStatus;

    private String parentPhone;

    private String extraInfo;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
