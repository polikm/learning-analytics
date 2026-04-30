package com.example.learning.model.vo;

import com.example.learning.model.entity.Question;
import lombok.Data;

/**
 * 试卷详情VO（含题目列表）
 */
@Data
public class PaperDetailVO {

    /**
     * 试卷ID
     */
    private Long id;

    /**
     * 试卷名称
     */
    private String paperName;

    /**
     * 学科ID
     */
    private Long subjectId;

    /**
     * 年级
     */
    private String gradeLevel;

    /**
     * 总分
     */
    private java.math.BigDecimal totalScore;

    /**
     * 题目数量
     */
    private Integer questionCount;

    /**
     * 平均难度
     */
    private java.math.BigDecimal difficultyAvg;

    /**
     * 考试时长
     */
    private Integer duration;

    /**
     * 试卷类型
     */
    private String paperType;

    /**
     * 及格分数
     */
    private java.math.BigDecimal passScore;

    /**
     * 题目列表
     */
    private java.util.List<PaperQuestionVO> questions;
}
