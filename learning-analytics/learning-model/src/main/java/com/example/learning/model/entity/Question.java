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
 * Question 实体类
 * 对应数据库表: question
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("question")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long subjectId;

    private String questionType;

    private String content;

    private String options;

    private String answer;

    private String analysis;

    private Integer difficulty;

    private String cognitiveLevel;

    private BigDecimal score;

    private String knowledgeIds;

    private String tags;

    private Long creatorId;

    private Long reviewerId;

    private Integer reviewStatus;

    private Integer usageCount;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
