package com.example.learning.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 智能组卷DTO
 */
@Data
public class AutoGeneratePaperDTO {

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
     * 知识点ID列表（逗号分隔）
     */
    private String knowledgeIds;

    /**
     * 难度（1-5）
     */
    private Integer difficulty;

    /**
     * 题型及数量比例，key为题型（single_choice/multiple_choice/true_false等），value为数量
     */
    private Map<Integer, Integer> typeRatio;

    /**
     * 总题目数
     */
    private Integer totalCount;

    /**
     * 考试时长（分钟）
     */
    private Integer duration;

    /**
     * 及格分数
     */
    private java.math.BigDecimal passScore;
}
