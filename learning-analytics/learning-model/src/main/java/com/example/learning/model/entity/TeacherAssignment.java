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

    private Long teacherId;

    private Long schoolId;

    private Long gradeId;

    private Long classId;

    private Long subjectId;

    @TableField("academic_year")
    private String academicYear;

    private String semester;

    private Integer status;

    private String remark;

    private Long tenantId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
