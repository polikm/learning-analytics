package com.example.learning.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 学生成绩趋势VO
 */
@Data
public class StudentTrendVO {

    /**
     * 测评ID
     */
    private Long examId;

    /**
     * 测评名称
     */
    private String examName;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 考试时间
     */
    private String examTime;
}
