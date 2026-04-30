package com.example.learning.service;

import java.util.List;
import java.util.Map;

/**
 * 学情预警服务接口
 */
public interface WarningService {

    /**
     * 检查学情预警（成绩连续下滑、活跃度降低等）
     *
     * @param studentId 学生ID
     * @return 预警信息列表
     */
    List<Map<String, Object>> checkWarnings(Long studentId);

    /**
     * 获取学生预警列表
     *
     * @param studentId 学生ID
     * @return 预警列表
     */
    List<Map<String, Object>> getWarnings(Long studentId);

    /**
     * 获取班级预警学生列表
     *
     * @param classId 班级ID
     * @return 预警学生列表
     */
    List<Map<String, Object>> getWarningsByClass(Long classId);
}
