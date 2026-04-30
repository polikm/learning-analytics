package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.*;
import com.example.learning.model.mapper.*;
import com.example.learning.service.KnowledgeMasteryService;
import com.example.learning.service.ProfileService;
import com.example.learning.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学情档案服务实现类
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private CertificateMapper certificateMapper;

    @Autowired
    private LearningBehaviorMapper learningBehaviorMapper;

    @Autowired
    private KnowledgeMasteryService knowledgeMasteryService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private ClassMapper classMapper;

    @Override
    public Map<String, Object> getStudentProfile(Long studentId) {
        // 1. 基础信息
        StudentProfile profile = studentProfileMapper.selectById(studentId);
        if (profile == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 2. 最近成绩（最近5次测评记录）
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getStudentId, studentId)
                .isNotNull(ExamRecord::getTotalScore)
                .orderByDesc(ExamRecord::getSubmitTime)
                .last("LIMIT 5");
        List<ExamRecord> recentRecords = examRecordMapper.selectList(recordWrapper);

        // 3. 知识掌握度
        Map<String, Object> masteryData = knowledgeMasteryService.getMasteryByStudent(studentId);

        // 4. 证书列表
        LambdaQueryWrapper<Certificate> certWrapper = new LambdaQueryWrapper<>();
        certWrapper.eq(Certificate::getStudentId, studentId)
                .eq(Certificate::getReviewStatus, 1)
                .orderByDesc(Certificate::getAwardDate);
        List<Certificate> certificates = certificateMapper.selectList(certWrapper);

        // 5. 预警信息
        List<Map<String, Object>> warnings = warningService.getWarnings(studentId);

        // 6. 学习行为统计（最近30天）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LambdaQueryWrapper<LearningBehavior> behaviorWrapper = new LambdaQueryWrapper<>();
        behaviorWrapper.eq(LearningBehavior::getStudentId, studentId)
                .ge(LearningBehavior::getBehaviorTime, thirtyDaysAgo);
        List<LearningBehavior> behaviors = learningBehaviorMapper.selectList(behaviorWrapper);
        int totalDuration = behaviors.stream()
                .filter(b -> b.getDuration() != null)
                .mapToInt(LearningBehavior::getDuration)
                .sum();

        // 组装结果
        Map<String, Object> result = new HashMap<>();
        result.put("profile", profile);
        result.put("recentRecords", recentRecords);
        result.put("knowledgeMastery", masteryData);
        result.put("certificates", certificates);
        result.put("warnings", warnings);
        result.put("recentBehaviorStats", Map.of(
                "totalDuration", totalDuration,
                "activityCount", behaviors.size()
        ));
        return result;
    }

    @Override
    public Map<String, Object> getClassOverview(Long classId) {
        // 获取班级下所有学生
        LambdaQueryWrapper<StudentProfile> profileWrapper = new LambdaQueryWrapper<>();
        profileWrapper.eq(StudentProfile::getClassId, classId);
        List<StudentProfile> students = studentProfileMapper.selectList(profileWrapper);

        if (students.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("students", Collections.emptyList());
            result.put("averageScore", BigDecimal.ZERO);
            result.put("warningCount", 0);
            result.put("totalStudents", 0);
            return result;
        }

        List<Long> studentIds = students.stream()
                .map(StudentProfile::getId)
                .collect(Collectors.toList());

        // 获取班级所有学生的最近一次成绩
        List<ExamRecord> latestRecords = new ArrayList<>();
        for (Long studentId : studentIds) {
            LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(ExamRecord::getStudentId, studentId)
                    .eq(ExamRecord::getClassId, classId)
                    .isNotNull(ExamRecord::getTotalScore)
                    .orderByDesc(ExamRecord::getSubmitTime)
                    .last("LIMIT 1");
            ExamRecord record = examRecordMapper.selectOne(recordWrapper);
            if (record != null) {
                latestRecords.add(record);
            }
        }

        // 计算平均分
        BigDecimal averageScore = BigDecimal.ZERO;
        if (!latestRecords.isEmpty()) {
            BigDecimal total = latestRecords.stream()
                    .map(ExamRecord::getTotalScore)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averageScore = total.divide(BigDecimal.valueOf(latestRecords.size()), 2, RoundingMode.HALF_UP);
        }

        // 统计预警人数
        int warningCount = 0;
        for (Long studentId : studentIds) {
            List<Map<String, Object>> warnings = warningService.getWarnings(studentId);
            if (!warnings.isEmpty()) {
                warningCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("students", students);
        result.put("averageScore", averageScore);
        result.put("warningCount", warningCount);
        result.put("totalStudents", students.size());
        return result;
    }

    @Override
    public Map<String, Object> getGradeOverview(Long gradeId) {
        // 获取年级下所有班级
        LambdaQueryWrapper<StudentProfile> profileWrapper = new LambdaQueryWrapper<>();
        profileWrapper.eq(StudentProfile::getGradeId, gradeId);
        List<StudentProfile> allStudents = studentProfileMapper.selectList(profileWrapper);

        // 按班级分组
        Map<Long, List<StudentProfile>> classGroups = allStudents.stream()
                .filter(s -> s.getClassId() != null)
                .collect(Collectors.groupingBy(StudentProfile::getClassId));

        List<Map<String, Object>> classOverviews = new ArrayList<>();
        for (Map.Entry<Long, List<StudentProfile>> entry : classGroups.entrySet()) {
            Map<String, Object> classOverview = getClassOverview(entry.getKey());
            classOverview.put("classId", entry.getKey());
            classOverviews.add(classOverview);
        }

        // 年级整体平均分
        BigDecimal gradeAverage = BigDecimal.ZERO;
        int totalStudents = allStudents.size();
        List<ExamRecord> allLatestRecords = new ArrayList<>();
        for (StudentProfile student : allStudents) {
            LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(ExamRecord::getStudentId, student.getId())
                    .isNotNull(ExamRecord::getTotalScore)
                    .orderByDesc(ExamRecord::getSubmitTime)
                    .last("LIMIT 1");
            ExamRecord record = examRecordMapper.selectOne(recordWrapper);
            if (record != null) {
                allLatestRecords.add(record);
            }
        }
        if (!allLatestRecords.isEmpty()) {
            BigDecimal total = allLatestRecords.stream()
                    .map(ExamRecord::getTotalScore)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            gradeAverage = total.divide(BigDecimal.valueOf(allLatestRecords.size()), 2, RoundingMode.HALF_UP);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("gradeId", gradeId);
        result.put("totalStudents", totalStudents);
        result.put("classCount", classGroups.size());
        result.put("gradeAverageScore", gradeAverage);
        result.put("classOverviews", classOverviews);
        return result;
    }
}
