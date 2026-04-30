package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.model.entity.ExamRecord;
import com.example.learning.model.entity.LearningBehavior;
import com.example.learning.model.entity.StudentProfile;
import com.example.learning.model.entity.StudentProfileSnapshot;
import com.example.learning.model.mapper.ExamRecordMapper;
import com.example.learning.model.mapper.LearningBehaviorMapper;
import com.example.learning.model.mapper.StudentProfileMapper;
import com.example.learning.model.mapper.StudentProfileSnapshotMapper;
import com.example.learning.service.KnowledgeMasteryService;
import com.example.learning.service.SnapshotService;
import com.example.learning.service.WarningService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学情快照服务实现类
 */
@Service
public class SnapshotServiceImpl implements SnapshotService {

    @Autowired
    private StudentProfileSnapshotMapper snapshotMapper;

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private LearningBehaviorMapper learningBehaviorMapper;

    @Autowired
    private KnowledgeMasteryService knowledgeMasteryService;

    @Autowired
    private WarningService warningService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentProfileSnapshot generateSnapshot(Long studentId) {
        StudentProfile profile = studentProfileMapper.selectById(studentId);
        if (profile == null) {
            throw new RuntimeException("学生档案不存在");
        }

        StudentProfileSnapshot snapshot = new StudentProfileSnapshot();
        snapshot.setTenantId(profile.getTenantId());
        snapshot.setStudentId(studentId);
        snapshot.setSnapshotDate(LocalDateTime.now());
        snapshot.setSnapshotType("AUTO");

        // 1. 学术成绩（最近一次测评成绩）
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getStudentId, studentId)
                .isNotNull(ExamRecord::getTotalScore)
                .orderByDesc(ExamRecord::getSubmitTime)
                .last("LIMIT 1");
        ExamRecord latestRecord = examRecordMapper.selectOne(recordWrapper);
        snapshot.setAcademicScore(latestRecord != null ? latestRecord.getTotalScore() : BigDecimal.ZERO);

        // 2. 行为得分（最近30天学习时长转换为分数，假设每天30分钟为满分100）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LambdaQueryWrapper<LearningBehavior> behaviorWrapper = new LambdaQueryWrapper<>();
        behaviorWrapper.eq(LearningBehavior::getStudentId, studentId)
                .ge(LearningBehavior::getBehaviorTime, thirtyDaysAgo);
        List<LearningBehavior> behaviors = learningBehaviorMapper.selectList(behaviorWrapper);
        int totalDuration = behaviors.stream()
                .filter(b -> b.getDuration() != null)
                .mapToInt(LearningBehavior::getDuration)
                .sum();
        // 转换为分数（30天 * 30分钟 = 900分钟为满分）
        BigDecimal behaviorScore = BigDecimal.valueOf(totalDuration)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);
        if (behaviorScore.compareTo(BigDecimal.valueOf(100)) > 0) {
            behaviorScore = BigDecimal.valueOf(100);
        }
        snapshot.setBehaviorScore(behaviorScore);

        // 3. 知识掌握度（JSON格式）
        try {
            Map<String, Object> masteryData = knowledgeMasteryService.getMasteryByStudent(studentId);
            snapshot.setKnowledgeMastery(objectMapper.writeValueAsString(masteryData));
        } catch (JsonProcessingException e) {
            snapshot.setKnowledgeMastery("{}");
        }

        // 4. 各科成绩（JSON格式）
        try {
            LambdaQueryWrapper<ExamRecord> allRecordsWrapper = new LambdaQueryWrapper<>();
            allRecordsWrapper.eq(ExamRecord::getStudentId, studentId)
                    .isNotNull(ExamRecord::getTotalScore)
                    .orderByDesc(ExamRecord::getSubmitTime)
                    .last("LIMIT 10");
            List<ExamRecord> recentRecords = examRecordMapper.selectList(allRecordsWrapper);
            snapshot.setSubjectScores(objectMapper.writeValueAsString(recentRecords));
        } catch (JsonProcessingException e) {
            snapshot.setSubjectScores("[]");
        }

        // 5. 趋势数据（最近10次成绩）
        try {
            LambdaQueryWrapper<ExamRecord> trendWrapper = new LambdaQueryWrapper<>();
            trendWrapper.eq(ExamRecord::getStudentId, studentId)
                    .isNotNull(ExamRecord::getTotalScore)
                    .orderByAsc(ExamRecord::getSubmitTime)
                    .last("LIMIT 10");
            List<ExamRecord> trendRecords = examRecordMapper.selectList(trendWrapper);
            List<Map<String, Object>> trendData = trendRecords.stream().map(r -> {
                Map<String, Object> point = new HashMap<>();
                point.put("date", r.getSubmitTime());
                point.put("score", r.getTotalScore());
                return point;
            }).collect(Collectors.toList());
            snapshot.setTrendData(objectMapper.writeValueAsString(trendData));
        } catch (JsonProcessingException e) {
            snapshot.setTrendData("[]");
        }

        // 6. 预警标记
        try {
            List<Map<String, Object>> warnings = warningService.checkWarnings(studentId);
            List<String> warningTypes = warnings.stream()
                    .map(w -> (String) w.get("warningType"))
                    .collect(Collectors.toList());
            snapshot.setWarningFlags(objectMapper.writeValueAsString(warningTypes));
        } catch (JsonProcessingException e) {
            snapshot.setWarningFlags("[]");
        }

        snapshot.setCreatedAt(LocalDateTime.now());
        snapshotMapper.insert(snapshot);
        return snapshot;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateClassSnapshots(Long classId) {
        LambdaQueryWrapper<StudentProfile> profileWrapper = new LambdaQueryWrapper<>();
        profileWrapper.eq(StudentProfile::getClassId, classId);
        List<StudentProfile> students = studentProfileMapper.selectList(profileWrapper);

        int count = 0;
        for (StudentProfile student : students) {
            try {
                generateSnapshot(student.getId());
                count++;
            } catch (Exception e) {
                // 单个学生快照生成失败不影响其他学生
            }
        }
        return count;
    }

    @Override
    public List<StudentProfileSnapshot> getSnapshots(Long studentId) {
        LambdaQueryWrapper<StudentProfileSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentProfileSnapshot::getStudentId, studentId)
                .orderByDesc(StudentProfileSnapshot::getSnapshotDate);
        return snapshotMapper.selectList(wrapper);
    }
}
