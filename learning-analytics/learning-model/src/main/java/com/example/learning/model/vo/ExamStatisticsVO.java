package com.example.learning.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 测评统计VO
 */
@Data
public class ExamStatisticsVO {

    /**
     * 测评ID
     */
    private Long examId;

    /**
     * 参加人数
     */
    private Integer totalStudents;

    /**
     * 已交卷人数
     */
    private Integer submittedCount;

    /**
     * 平均分
     */
    private BigDecimal avgScore;

    /**
     * 最高分
     */
    private BigDecimal maxScore;

    /**
     * 最低分
     */
    private BigDecimal minScore;

    /**
     * 及格率（百分比）
     */
    private BigDecimal passRate;

    /**
     * 及格分数
     */
    private BigDecimal passScore;

    /**
     * 分数段分布，key为分数段描述，value为人数
     * 例如: {"90-100": 10, "80-89": 20, ...}
     */
    private Map<String, Integer> scoreDistribution;
}
