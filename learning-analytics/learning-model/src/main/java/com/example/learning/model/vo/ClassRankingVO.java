package com.example.learning.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 班级排名VO
 */
@Data
public class ClassRankingVO {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 用时（秒）
     */
    private Integer timeUsed;
}
