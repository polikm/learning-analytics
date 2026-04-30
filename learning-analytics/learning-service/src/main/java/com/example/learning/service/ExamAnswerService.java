package com.example.learning.service;

import com.example.learning.model.entity.ExamAnswer;

import java.util.List;

/**
 * 测评答案服务接口
 */
public interface ExamAnswerService {

    /**
     * 保存答案
     *
     * @param recordId      答题记录ID
     * @param questionId    题目ID
     * @param studentAnswer 学生答案
     */
    void saveAnswer(Long recordId, Long questionId, String studentAnswer);

    /**
     * 查询答题明细
     *
     * @param recordId 答题记录ID
     * @return 答案列表
     */
    List<ExamAnswer> getAnswersByRecord(Long recordId);
}
