package com.example.learning.service;

/**
 * 阅卷服务接口
 */
public interface GradingService {

    /**
     * 自动阅卷（客观题：单选/多选/判断自动评分，对比studentAnswer和question.answer）
     *
     * @param examRecordId 答题记录ID
     */
    void autoGrade(Long examRecordId);

    /**
     * 批量自动阅卷（遍历该测评所有已交卷记录）
     *
     * @param examId 测评ID
     */
    void autoGradeExam(Long examId);
}
