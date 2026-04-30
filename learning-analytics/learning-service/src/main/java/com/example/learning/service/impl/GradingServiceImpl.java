package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.model.entity.Exam;
import com.example.learning.model.entity.ExamAnswer;
import com.example.learning.model.entity.ExamRecord;
import com.example.learning.model.entity.Paper;
import com.example.learning.model.entity.PaperQuestion;
import com.example.learning.model.entity.Question;
import com.example.learning.model.mapper.ExamAnswerMapper;
import com.example.learning.model.mapper.ExamMapper;
import com.example.learning.model.mapper.ExamRecordMapper;
import com.example.learning.model.mapper.PaperMapper;
import com.example.learning.model.mapper.PaperQuestionMapper;
import com.example.learning.model.mapper.QuestionMapper;
import com.example.learning.service.GradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 阅卷服务实现类
 */
@Service
public class GradingServiceImpl implements GradingService {

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private ExamAnswerMapper examAnswerMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoGrade(Long examRecordId) {
        ExamRecord record = examRecordMapper.selectById(examRecordId);
        if (record == null) {
            throw new RuntimeException("答题记录不存在");
        }

        // 获取测评关联的试卷
        Exam exam = examMapper.selectById(record.getExamId());
        if (exam == null || exam.getPaperId() == null) {
            throw new RuntimeException("测评信息异常");
        }

        Paper paper = paperMapper.selectById(exam.getPaperId());
        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 获取试卷的所有题目关联
        LambdaQueryWrapper<PaperQuestion> pqWrapper = new LambdaQueryWrapper<>();
        pqWrapper.eq(PaperQuestion::getPaperId, paper.getId());
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(pqWrapper);

        // 获取学生的所有答案
        LambdaQueryWrapper<ExamAnswer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.eq(ExamAnswer::getRecordId, examRecordId);
        List<ExamAnswer> answers = examAnswerMapper.selectList(answerWrapper);

        BigDecimal objectiveScore = BigDecimal.ZERO;
        BigDecimal subjectiveScore = BigDecimal.ZERO;

        // 遍历每道题目进行自动评分
        for (PaperQuestion pq : paperQuestions) {
            Question question = questionMapper.selectById(pq.getQuestionId());
            if (question == null) {
                continue;
            }

            // 查找该题目的学生答案
            ExamAnswer studentAnswer = answers.stream()
                    .filter(a -> a.getQuestionId().equals(pq.getQuestionId()))
                    .findFirst()
                    .orElse(null);

            if (studentAnswer == null) {
                // 学生未作答，记录0分
                ExamAnswer newAnswer = new ExamAnswer();
                newAnswer.setRecordId(examRecordId);
                newAnswer.setQuestionId(pq.getQuestionId());
                newAnswer.setStudentAnswer("");
                newAnswer.setIsCorrect(0);
                newAnswer.setScore(BigDecimal.ZERO);
                newAnswer.setFullScore(pq.getScore());
                examAnswerMapper.insert(newAnswer);
                continue;
            }

            String qType = question.getQuestionType();
            // 客观题自动评分：单选(single_choice)、多选(multiple_choice)、判断(true_false)
            if ("single_choice".equals(qType) || "multiple_choice".equals(qType) || "true_false".equals(qType)) {
                String standardAnswer = question.getAnswer();
                String studentAns = studentAnswer.getStudentAnswer();

                // 对比答案（忽略大小写和空格）
                boolean isCorrect = false;
                if (standardAnswer != null && studentAns != null) {
                    isCorrect = standardAnswer.trim().equalsIgnoreCase(studentAns.trim());
                }

                BigDecimal score = isCorrect ? pq.getScore() : BigDecimal.ZERO;
                studentAnswer.setIsCorrect(isCorrect ? 1 : 0);
                studentAnswer.setScore(score);
                studentAnswer.setFullScore(pq.getScore());
                examAnswerMapper.updateById(studentAnswer);

                objectiveScore = objectiveScore.add(score);
            } else {
                // 主观题不自动评分，标记为主观题待人工阅卷
                studentAnswer.setFullScore(pq.getScore());
                studentAnswer.setIsCorrect(null); // 未评分
                examAnswerMapper.updateById(studentAnswer);
                subjectiveScore = subjectiveScore.add(BigDecimal.ZERO); // 暂记0分
            }
        }

        // 更新答题记录的总分
        record.setObjectiveScore(objectiveScore);
        record.setSubjectiveScore(subjectiveScore);
        record.setTotalScore(objectiveScore.add(subjectiveScore));
        record.setUpdatedAt(java.time.LocalDateTime.now());
        examRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoGradeExam(Long examId) {
        // 查询该测评所有已交卷且未阅卷的记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStatus, 1); // 已交卷
        List<ExamRecord> records = examRecordMapper.selectList(wrapper);

        for (ExamRecord record : records) {
            try {
                autoGrade(record.getId());
            } catch (Exception e) {
                // 单条记录阅卷失败不影响其他记录
                // 实际项目中应记录日志
            }
        }
    }
}
