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
 * Paper 实体类
 * 对应数据库表: paper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("paper")
public class Paper {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String paperName;

    private Long subjectId;

    private String gradeLevel;

    private BigDecimal totalScore;

    private Integer questionCount;

    private BigDecimal difficultyAvg;

    private Integer duration;

    private String paperType;

    private BigDecimal passScore;

    private String sections;

    private Long creatorId;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
