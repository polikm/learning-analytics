package com.example.learning.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ExamRecord 实体类
 * 对应数据库表: exam_record
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam_record")
public class ExamRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long examId;

    private Long studentId;

    private Long classId;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime submitTime;

    private BigDecimal totalScore;

    private BigDecimal objectiveScore;

    private BigDecimal subjectiveScore;

    private Integer rankInClass;

    private Integer rankInGrade;

    private BigDecimal percentile;

    private Integer timeUsed;

    private String cheatFlags;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
