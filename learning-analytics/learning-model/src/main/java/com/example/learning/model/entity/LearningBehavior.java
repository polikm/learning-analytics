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
 * LearningBehavior 实体类
 * 对应数据库表: learning_behavior
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("learning_behavior")
public class LearningBehavior {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long studentId;

    private String sourceSystem;

    private String behaviorType;

    private Long subjectId;

    private String resourceId;

    private String resourceName;

    private Integer duration;

    private BigDecimal score;

    private String detailJson;

    private LocalDateTime behaviorTime;

    private LocalDateTime syncTime;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
