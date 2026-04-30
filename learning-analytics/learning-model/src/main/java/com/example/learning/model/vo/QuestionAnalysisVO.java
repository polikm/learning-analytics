package com.example.learning.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 题目分析VO
 */
@Data
public class QuestionAnalysisVO {

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题型
     */
    private String questionType;

    /**
     * 正确人数
     */
    private Integer correctCount;

    /**
     * 总答题人数
     */
    private Integer totalCount;

    /**
     * 正确率（百分比）
     */
    private BigDecimal correctRate;

    /**
     * 区分度
     */
    private BigDecimal discrimination;

    /**
     * 平均得分
     */
    private BigDecimal avgScore;

    /**
     * 满分
     */
    private BigDecimal fullScore;
}
