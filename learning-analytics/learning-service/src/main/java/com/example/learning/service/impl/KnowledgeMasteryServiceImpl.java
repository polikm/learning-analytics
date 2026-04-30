package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.model.entity.Exam;
import com.example.learning.model.entity.ExamAnswer;
import com.example.learning.model.entity.ExamRecord;
import com.example.learning.model.entity.KnowledgePoint;
import com.example.learning.model.entity.Paper;
import com.example.learning.model.entity.Question;
import com.example.learning.model.entity.Subject;
import com.example.learning.model.mapper.ExamAnswerMapper;
import com.example.learning.model.mapper.ExamMapper;
import com.example.learning.model.mapper.ExamRecordMapper;
import com.example.learning.model.mapper.KnowledgePointMapper;
import com.example.learning.model.mapper.PaperMapper;
import com.example.learning.model.mapper.QuestionMapper;
import com.example.learning.model.mapper.SubjectMapper;
import com.example.learning.service.KnowledgeMasteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识掌握度服务实现类
 */
@Service
public class KnowledgeMasteryServiceImpl implements KnowledgeMasteryService {

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private ExamAnswerMapper examAnswerMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Override
    public List<Map<String, Object>> calculateMastery(Long studentId, Long subjectId) {
        // 1. 获取该学科下所有知识点
        LambdaQueryWrapper<KnowledgePoint> kpWrapper = new LambdaQueryWrapper<>();
        kpWrapper.eq(KnowledgePoint::getSubjectId, subjectId)
                .eq(KnowledgePoint::getStatus, 1);
        List<KnowledgePoint> knowledgePoints = knowledgePointMapper.selectList(kpWrapper);

        if (knowledgePoints.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 获取该学科下的所有试卷ID（通过Paper关联subjectId）
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getSubjectId, subjectId)
                .eq(Paper::getStatus, 1);
        List<Paper> papers = paperMapper.selectList(paperWrapper);
        List<Long> paperIds = papers.stream().map(Paper::getId).collect(Collectors.toList());

        if (paperIds.isEmpty()) {
            return knowledgePoints.stream().map(kp -> {
                Map<String, Object> item = new HashMap<>();
                item.put("knowledgePointId", kp.getId());
                item.put("knowledgePointName", kp.getKpName());
                item.put("masteryRate", BigDecimal.ZERO);
                item.put("totalQuestions", 0);
                item.put("correctQuestions", 0);
                return item;
            }).collect(Collectors.toList());
        }

        // 3. 通过试卷ID获取已结束的测评ID
        LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<>();
        examWrapper.in(Exam::getPaperId, paperIds)
                .eq(Exam::getStatus, 2); // 已结束
        List<Exam> exams = examMapper.selectList(examWrapper);
        List<Long> examIds = exams.stream().map(Exam::getId).collect(Collectors.toList());

        if (examIds.isEmpty()) {
            return knowledgePoints.stream().map(kp -> {
                Map<String, Object> item = new HashMap<>();
                item.put("knowledgePointId", kp.getId());
                item.put("knowledgePointName", kp.getKpName());
                item.put("masteryRate", BigDecimal.ZERO);
                item.put("totalQuestions", 0);
                item.put("correctQuestions", 0);
                return item;
            }).collect(Collectors.toList());
        }

        // 4. 获取该学生这些测评的答题记录
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getStudentId, studentId)
                .in(ExamRecord::getExamId, examIds)
                .isNotNull(ExamRecord::getSubmitTime);
        List<ExamRecord> records = examRecordMapper.selectList(recordWrapper);
        List<Long> recordIds = records.stream().map(ExamRecord::getId).collect(Collectors.toList());

        if (recordIds.isEmpty()) {
            return knowledgePoints.stream().map(kp -> {
                Map<String, Object> item = new HashMap<>();
                item.put("knowledgePointId", kp.getId());
                item.put("knowledgePointName", kp.getKpName());
                item.put("masteryRate", BigDecimal.ZERO);
                item.put("totalQuestions", 0);
                item.put("correctQuestions", 0);
                return item;
            }).collect(Collectors.toList());
        }

        // 5. 获取所有答题详情
        LambdaQueryWrapper<ExamAnswer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.in(ExamAnswer::getRecordId, recordIds);
        List<ExamAnswer> answers = examAnswerMapper.selectList(answerWrapper);

        // 6. 获取所有题目（关联知识点）
        List<Long> questionIds = answers.stream()
                .map(ExamAnswer::getQuestionId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Question> questionMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            questionMap = questionMapper.selectBatchIds(questionIds).stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));
        }

        // 7. 按知识点统计正确率
        Map<Long, int[]> kpStats = new HashMap<>(); // knowledgePointId -> [total, correct]
        for (ExamAnswer answer : answers) {
            Question question = questionMap.get(answer.getQuestionId());
            if (question == null || question.getKnowledgeIds() == null) {
                continue;
            }
            // 题目关联的知识点ID（逗号分隔）
            String[] kpIdStrs = question.getKnowledgeIds().split(",");
            for (String kpIdStr : kpIdStrs) {
                try {
                    Long kpId = Long.parseLong(kpIdStr.trim());
                    kpStats.computeIfAbsent(kpId, k -> new int[2]);
                    kpStats.get(kpId)[0]++; // total
                    if (answer.getIsCorrect() != null && answer.getIsCorrect() == 1) {
                        kpStats.get(kpId)[1]++; // correct
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // 8. 组装结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (KnowledgePoint kp : knowledgePoints) {
            Map<String, Object> item = new HashMap<>();
            item.put("knowledgePointId", kp.getId());
            item.put("knowledgePointName", kp.getKpName());
            item.put("kpCode", kp.getKpCode());
            int[] stats = kpStats.getOrDefault(kp.getId(), new int[2]);
            int total = stats[0];
            int correct = stats[1];
            BigDecimal masteryRate = total > 0
                    ? BigDecimal.valueOf(correct).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            item.put("masteryRate", masteryRate);
            item.put("totalQuestions", total);
            item.put("correctQuestions", correct);
            result.add(item);
        }

        return result;
    }

    @Override
    public Map<String, Object> getMasteryByStudent(Long studentId) {
        // 获取所有学科
        List<Subject> subjects = subjectMapper.selectList(new LambdaQueryWrapper<>());

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> subjectMasteryList = new ArrayList<>();

        for (Subject subject : subjects) {
            List<Map<String, Object>> masteryList = calculateMastery(studentId, subject.getId());
            if (!masteryList.isEmpty()) {
                // 计算学科平均掌握度
                BigDecimal avgRate = masteryList.stream()
                        .map(m -> (BigDecimal) m.get("masteryRate"))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (!masteryList.isEmpty()) {
                    avgRate = avgRate.divide(BigDecimal.valueOf(masteryList.size()), 4, RoundingMode.HALF_UP);
                }

                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("subjectId", subject.getId());
                subjectData.put("subjectName", subject.getSubjectName());
                subjectData.put("averageMasteryRate", avgRate);
                subjectData.put("knowledgePoints", masteryList);
                subjectMasteryList.add(subjectData);
            }
        }

        result.put("studentId", studentId);
        result.put("subjects", subjectMasteryList);
        return result;
    }
}
