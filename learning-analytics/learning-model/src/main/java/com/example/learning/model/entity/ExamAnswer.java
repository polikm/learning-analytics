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
 * ExamAnswer 实体类
 * 对应数据库表: exam_answer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("exam_answer")
public class ExamAnswer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recordId;

    private Long questionId;

    private String studentAnswer;

    private Integer isCorrect;

    private BigDecimal score;

    private BigDecimal fullScore;

    private BigDecimal teacherScore;

    private Long gradedBy;

    private LocalDateTime answerTime;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
