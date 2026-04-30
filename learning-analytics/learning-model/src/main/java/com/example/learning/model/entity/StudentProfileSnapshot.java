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
 * StudentProfileSnapshot 实体类
 * 对应数据库表: student_profile_snapshot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("student_profile_snapshot")
public class StudentProfileSnapshot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long studentId;

    private LocalDateTime snapshotDate;

    private String snapshotType;

    private BigDecimal academicScore;

    private BigDecimal behaviorScore;

    private String knowledgeMastery;

    private String subjectScores;

    private String trendData;

    private String warningFlags;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
