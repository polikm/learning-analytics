package com.example.learning.model.dto;

import lombok.Data;

/**
 * 保存答案DTO
 */
@Data
public class ExamAnswerDTO {

    /**
     * 答题记录ID
     */
    private Long recordId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 学生答案
     */
    private String studentAnswer;
}
