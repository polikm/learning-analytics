package com.example.learning.service;

import java.util.List;
import java.util.Map;

/**
 * 知识掌握度服务接口
 */
public interface KnowledgeMasteryService {

    /**
     * 计算知识点掌握度（基于该学生该学科所有测评答题数据，按知识点统计正确率）
     *
     * @param studentId 学生ID
     * @param subjectId 学科ID
     * @return 知识点掌握度列表
     */
    List<Map<String, Object>> calculateMastery(Long studentId, Long subjectId);

    /**
     * 获取学生所有学科的知识掌握度
     *
     * @param studentId 学生ID
     * @return 各学科知识掌握度数据
     */
    Map<String, Object> getMasteryByStudent(Long studentId);
}
