package com.example.learning.service;

import java.util.List;
import java.util.Map;

/**
 * 学情档案服务接口
 */
public interface ProfileService {

    /**
     * 获取学生学情档案（聚合基础信息+最近成绩+知识掌握度+证书列表+预警信息）
     *
     * @param studentId 学生ID
     * @return 学情档案数据
     */
    Map<String, Object> getStudentProfile(Long studentId);

    /**
     * 班级学情概览（学生列表+平均分+预警人数）
     *
     * @param classId 班级ID
     * @return 班级概览数据
     */
    Map<String, Object> getClassOverview(Long classId);

    /**
     * 年级学情概览
     *
     * @param gradeId 年级ID
     * @return 年级概览数据
     */
    Map<String, Object> getGradeOverview(Long gradeId);
}
