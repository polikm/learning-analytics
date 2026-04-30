package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.entity.LearningBehavior;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 学习行为服务接口
 */
public interface LearningBehaviorService {

    /**
     * 分页查询学习行为
     *
     * @param studentId    学生ID
     * @param behaviorType 行为类型
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param page         当前页码
     * @param size         每页大小
     * @return 分页结果
     */
    Page<LearningBehavior> getPage(Long studentId, String behaviorType,
                                   LocalDateTime startTime, LocalDateTime endTime,
                                   int page, int size);

    /**
     * 学习行为统计（总学习时长、活跃天数、行为类型分布）
     *
     * @param studentId 学生ID
     * @return 统计结果
     */
    Map<String, Object> getStatistics(Long studentId);

    /**
     * 按天聚合学习行为趋势
     *
     * @param studentId 学生ID
     * @param days      天数
     * @return 趋势数据
     */
    List<Map<String, Object>> getTrend(Long studentId, Integer days);

    /**
     * 记录学习行为
     *
     * @param behavior 学习行为实体
     */
    void recordBehavior(LearningBehavior behavior);
}
