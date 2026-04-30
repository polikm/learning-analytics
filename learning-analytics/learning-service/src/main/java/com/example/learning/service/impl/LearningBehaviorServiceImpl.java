package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.entity.LearningBehavior;
import com.example.learning.model.mapper.LearningBehaviorMapper;
import com.example.learning.service.LearningBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习行为服务实现类
 */
@Service
public class LearningBehaviorServiceImpl implements LearningBehaviorService {

    @Autowired
    private LearningBehaviorMapper learningBehaviorMapper;

    @Override
    public Page<LearningBehavior> getPage(Long studentId, String behaviorType,
                                          LocalDateTime startTime, LocalDateTime endTime,
                                          int page, int size) {
        LambdaQueryWrapper<LearningBehavior> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(LearningBehavior::getStudentId, studentId);
        }
        if (behaviorType != null && !behaviorType.isEmpty()) {
            wrapper.eq(LearningBehavior::getBehaviorType, behaviorType);
        }
        if (startTime != null) {
            wrapper.ge(LearningBehavior::getBehaviorTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(LearningBehavior::getBehaviorTime, endTime);
        }
        wrapper.orderByDesc(LearningBehavior::getBehaviorTime);
        return learningBehaviorMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Map<String, Object> getStatistics(Long studentId) {
        LambdaQueryWrapper<LearningBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningBehavior::getStudentId, studentId);
        List<LearningBehavior> behaviors = learningBehaviorMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();

        if (behaviors.isEmpty()) {
            result.put("totalDuration", 0);
            result.put("activeDays", 0);
            result.put("totalCount", 0);
            result.put("behaviorTypeDistribution", new HashMap<>());
            return result;
        }

        // 总学习时长（秒）
        int totalDuration = behaviors.stream()
                .filter(b -> b.getDuration() != null)
                .mapToInt(LearningBehavior::getDuration)
                .sum();
        result.put("totalDuration", totalDuration);
        result.put("totalDurationFormatted", formatDuration(totalDuration));

        // 活跃天数
        long activeDays = behaviors.stream()
                .map(LearningBehavior::getBehaviorTime)
                .filter(Objects::nonNull)
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .count();
        result.put("activeDays", activeDays);

        // 总行为次数
        result.put("totalCount", behaviors.size());

        // 行为类型分布
        Map<String, Long> typeDistribution = behaviors.stream()
                .filter(b -> b.getBehaviorType() != null)
                .collect(Collectors.groupingBy(LearningBehavior::getBehaviorType, Collectors.counting()));
        result.put("behaviorTypeDistribution", typeDistribution);

        return result;
    }

    @Override
    public List<Map<String, Object>> getTrend(Long studentId, Integer days) {
        if (days == null || days <= 0) {
            days = 7;
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.plusDays(1).atStartOfDay();

        LambdaQueryWrapper<LearningBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningBehavior::getStudentId, studentId)
                .ge(LearningBehavior::getBehaviorTime, startTime)
                .lt(LearningBehavior::getBehaviorTime, endTime);
        List<LearningBehavior> behaviors = learningBehaviorMapper.selectList(wrapper);

        // 按天分组
        Map<LocalDate, List<LearningBehavior>> groupedByDate = behaviors.stream()
                .filter(b -> b.getBehaviorTime() != null)
                .collect(Collectors.groupingBy(b -> b.getBehaviorTime().toLocalDate(),
                        TreeMap::new, Collectors.toList()));

        List<Map<String, Object>> trendList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            List<LearningBehavior> dayBehaviors = groupedByDate.getOrDefault(date, Collections.emptyList());

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.format(formatter));
            dayData.put("count", dayBehaviors.size());

            int dayDuration = dayBehaviors.stream()
                    .filter(b -> b.getDuration() != null)
                    .mapToInt(LearningBehavior::getDuration)
                    .sum();
            dayData.put("duration", dayDuration);

            trendList.add(dayData);
        }

        return trendList;
    }

    @Override
    public void recordBehavior(LearningBehavior behavior) {
        if (behavior.getBehaviorTime() == null) {
            behavior.setBehaviorTime(LocalDateTime.now());
        }
        if (behavior.getCreatedAt() == null) {
            behavior.setCreatedAt(LocalDateTime.now());
        }
        if (behavior.getUpdatedAt() == null) {
            behavior.setUpdatedAt(LocalDateTime.now());
        }
        learningBehaviorMapper.insert(behavior);
    }

    /**
     * 格式化时长（秒 -> X小时X分X秒）
     */
    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            return hours + "小时" + minutes + "分" + seconds + "秒";
        } else if (minutes > 0) {
            return minutes + "分" + seconds + "秒";
        } else {
            return seconds + "秒";
        }
    }
}
