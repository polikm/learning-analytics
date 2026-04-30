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
 * Exam 实体类
 * 对应数据库表: exam
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam")
public class Exam {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String examName;

    private Long paperId;

    private String examType;

    private String examMode;

    private Long schoolId;

    private Long gradeId;

    private String classIds;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long creatorId;

    private Integer status;

    private String configJson;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
