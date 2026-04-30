package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.model.entity.ExamRecord;
import com.example.learning.model.entity.LearningBehavior;
import com.example.learning.model.entity.StudentProfile;
import com.example.learning.model.mapper.ExamRecordMapper;
import com.example.learning.model.mapper.LearningBehaviorMapper;
import com.example.learning.model.mapper.StudentProfileMapper;
import com.example.learning.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 学情预警服务实现类
 */
@Service
public class WarningServiceImpl implements WarningService {

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private LearningBehaviorMapper learningBehaviorMapper;

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Override
    public List<Map<String, Object>> checkWarnings(Long studentId) {
        List<Map<String, Object>> warnings = new ArrayList<>();

        // 1. 成绩连续下滑检测（最近3次测评）
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getStudentId, studentId)
                .isNotNull(ExamRecord::getTotalScore)
                .orderByDesc(ExamRecord::getSubmitTime)
                .last("LIMIT 3");
        List<ExamRecord> recentRecords = examRecordMapper.selectList(recordWrapper);

        if (recentRecords.size() >= 3) {
            // 按时间正序排列
            recentRecords.sort(Comparator.comparing(ExamRecord::getSubmitTime));
            boolean declining = true;
            for (int i = 1; i < recentRecords.size(); i++) {
                BigDecimal prev = recentRecords.get(i - 1).getTotalScore();
                BigDecimal curr = recentRecords.get(i).getTotalScore();
                if (curr.compareTo(prev) >= 0) {
                    declining = false;
                    break;
                }
            }
            if (declining) {
                Map<String, Object> warning = new HashMap<>();
                warning.put("warningType", "SCORE_DECLINING");
                warning.put("warningLevel", "HIGH");
                warning.put("message", "成绩连续下滑，请关注学生学习状态");
                warning.put("latestScore", recentRecords.get(recentRecords.size() - 1).getTotalScore());
                warning.put("detectedAt", LocalDateTime.now());
                warnings.add(warning);
            }
        }

        // 2. 活跃度降低检测（最近14天 vs 前14天）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recentStart = now.minusDays(14);
        LocalDateTime previousStart = now.minusDays(28);

        LambdaQueryWrapper<LearningBehavior> recentBehaviorWrapper = new LambdaQueryWrapper<>();
        recentBehaviorWrapper.eq(LearningBehavior::getStudentId, studentId)
                .ge(LearningBehavior::getBehaviorTime, recentStart);
        Long recentCount = learningBehaviorMapper.selectCount(recentBehaviorWrapper);

        LambdaQueryWrapper<LearningBehavior> previousBehaviorWrapper = new LambdaQueryWrapper<>();
        previousBehaviorWrapper.eq(LearningBehavior::getStudentId, studentId)
                .ge(LearningBehavior::getBehaviorTime, previousStart)
                .lt(LearningBehavior::getBehaviorTime, recentStart);
        Long previousCount = learningBehaviorMapper.selectCount(previousBehaviorWrapper);

        if (previousCount > 0 && recentCount < previousCount * 0.5) {
            Map<String, Object> warning = new HashMap<>();
            warning.put("warningType", "LOW_ACTIVITY");
            warning.put("warningLevel", "MEDIUM");
            warning.put("message", "学习活跃度明显降低，请及时关注");
            warning.put("recentActivityCount", recentCount);
            warning.put("previousActivityCount", previousCount);
            warning.put("detectedAt", LocalDateTime.now());
            warnings.add(warning);
        }

        // 3. 成绩低于班级平均分检测
        StudentProfile profile = studentProfileMapper.selectById(studentId);
        if (profile != null && profile.getClassId() != null) {
            // 获取学生最近一次成绩
            LambdaQueryWrapper<ExamRecord> studentRecordWrapper = new LambdaQueryWrapper<>();
            studentRecordWrapper.eq(ExamRecord::getStudentId, studentId)
                    .eq(ExamRecord::getClassId, profile.getClassId())
                    .isNotNull(ExamRecord::getTotalScore)
                    .orderByDesc(ExamRecord::getSubmitTime)
                    .last("LIMIT 1");
            ExamRecord studentRecord = examRecordMapper.selectOne(studentRecordWrapper);

            if (studentRecord != null) {
                // 获取班级平均分
                LambdaQueryWrapper<ExamRecord> classRecordWrapper = new LambdaQueryWrapper<>();
                classRecordWrapper.eq(ExamRecord::getClassId, profile.getClassId())
                        .eq(ExamRecord::getExamId, studentRecord.getExamId())
                        .isNotNull(ExamRecord::getTotalScore);
                List<ExamRecord> classRecords = examRecordMapper.selectList(classRecordWrapper);

                if (classRecords.size() > 1) {
                    BigDecimal classAvg = classRecords.stream()
                            .map(ExamRecord::getTotalScore)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(classRecords.size()), 2, RoundingMode.HALF_UP);

                    if (studentRecord.getTotalScore().compareTo(classAvg.multiply(BigDecimal.valueOf(0.6))) < 0) {
                        Map<String, Object> warning = new HashMap<>();
                        warning.put("warningType", "BELOW_AVERAGE");
                        warning.put("warningLevel", "HIGH");
                        warning.put("message", "成绩远低于班级平均水平，需要重点关注");
                        warning.put("studentScore", studentRecord.getTotalScore());
                        warning.put("classAverage", classAvg);
                        warning.put("detectedAt", LocalDateTime.now());
                        warnings.add(warning);
                    }
                }
            }
        }

        return warnings;
    }

    @Override
    public List<Map<String, Object>> getWarnings(Long studentId) {
        return checkWarnings(studentId);
    }

    @Override
    public List<Map<String, Object>> getWarningsByClass(Long classId) {
        // 获取班级下所有学生
        LambdaQueryWrapper<StudentProfile> profileWrapper = new LambdaQueryWrapper<>();
        profileWrapper.eq(StudentProfile::getClassId, classId);
        List<StudentProfile> students = studentProfileMapper.selectList(profileWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentProfile student : students) {
            List<Map<String, Object>> warnings = checkWarnings(student.getId());
            if (!warnings.isEmpty()) {
                Map<String, Object> studentWarning = new HashMap<>();
                studentWarning.put("studentId", student.getId());
                studentWarning.put("studentNo", student.getStudentNo());
                studentWarning.put("warningCount", warnings.size());
                studentWarning.put("warnings", warnings);
                result.add(studentWarning);
            }
        }

        return result;
    }
}
