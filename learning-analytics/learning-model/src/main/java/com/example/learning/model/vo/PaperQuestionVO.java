package com.example.learning.model.vo;

import com.example.learning.model.entity.Question;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 试卷题目VO
 */
@Data
public class PaperQuestionVO {

    /**
     * 试卷题目关联ID
     */
    private Long id;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 大题序号
     */
    private Integer sectionIndex;

    /**
     * 题目序号
     */
    private Integer questionSeq;

    /**
     * 分值
     */
    private BigDecimal score;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 题目详情
     */
    private Question question;
}
