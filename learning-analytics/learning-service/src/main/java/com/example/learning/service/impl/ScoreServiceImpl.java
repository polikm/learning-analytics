package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
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
import com.example.learning.model.vo.ClassRankingVO;
import com.example.learning.model.vo.ExamStatisticsVO;
import com.example.learning.model.vo.QuestionAnalysisVO;
import com.example.learning.model.vo.StudentTrendVO;
import com.example.learning.service.ScoreService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 成绩服务实现类
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private ExamAnswerMapper examAnswerMapper;

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public ExamStatisticsVO getStatistics(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("测评不存在");
        }

        Paper paper = paperMapper.selectById(exam.getPaperId());

        // 查询所有已交卷的记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStatus, 1);
        List<ExamRecord> records = examRecordMapper.selectList(wrapper);

        ExamStatisticsVO vo = new ExamStatisticsVO();
        vo.setExamId(examId);
        vo.setSubmittedCount(records.size());
        vo.setPassScore(paper != null ? paper.getPassScore() : BigDecimal.valueOf(60));

        if (records.isEmpty()) {
            vo.setTotalStudents(0);
            vo.setAvgScore(BigDecimal.ZERO);
            vo.setMaxScore(BigDecimal.ZERO);
            vo.setMinScore(BigDecimal.ZERO);
            vo.setPassRate(BigDecimal.ZERO);
            vo.setScoreDistribution(new HashMap<>());
            return vo;
        }

        // 计算统计数据
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal maxScore = BigDecimal.ZERO;
        BigDecimal minScore = new BigDecimal("999999");
        int passCount = 0;

        // 分数段分布
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("90-100", 0);
        distribution.put("80-89", 0);
        distribution.put("70-79", 0);
        distribution.put("60-69", 0);
        distribution.put("0-59", 0);

        BigDecimal passScore = vo.getPassScore();

        for (ExamRecord record : records) {
            BigDecimal score = record.getTotalScore() != null ? record.getTotalScore() : BigDecimal.ZERO;
            totalScore = totalScore.add(score);

            if (score.compareTo(maxScore) > 0) {
                maxScore = score;
            }
            if (score.compareTo(minScore) < 0) {
                minScore = score;
            }
            if (score.compareTo(passScore) >= 0) {
                passCount++;
            }

            // 分数段统计
            double scoreVal = score.doubleValue();
            if (scoreVal >= 90) {
                distribution.merge("90-100", 1, Integer::sum);
            } else if (scoreVal >= 80) {
                distribution.merge("80-89", 1, Integer::sum);
            } else if (scoreVal >= 70) {
                distribution.merge("70-79", 1, Integer::sum);
            } else if (scoreVal >= 60) {
                distribution.merge("60-69", 1, Integer::sum);
            } else {
                distribution.merge("0-59", 1, Integer::sum);
            }
        }

        BigDecimal avgScore = totalScore.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
        BigDecimal passRate = BigDecimal.valueOf(passCount)
                .divide(BigDecimal.valueOf(records.size()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        vo.setTotalStudents(records.size());
        vo.setAvgScore(avgScore);
        vo.setMaxScore(maxScore);
        vo.setMinScore(minScore);
        vo.setPassRate(passRate);
        vo.setScoreDistribution(distribution);

        return vo;
    }

    @Override
    public List<QuestionAnalysisVO> getQuestionAnalysis(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("测评不存在");
        }

        Paper paper = paperMapper.selectById(exam.getPaperId());
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }

        // 获取试卷题目
        LambdaQueryWrapper<PaperQuestion> pqWrapper = new LambdaQueryWrapper<>();
        pqWrapper.eq(PaperQuestion::getPaperId, paper.getId())
                .orderByAsc(PaperQuestion::getSortOrder);
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(pqWrapper);

        // 获取所有已交卷记录
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStatus, 1);
        List<ExamRecord> records = examRecordMapper.selectList(recordWrapper);

        List<QuestionAnalysisVO> analysisList = new ArrayList<>();
        int totalStudents = records.size();

        for (PaperQuestion pq : paperQuestions) {
            Question question = questionMapper.selectById(pq.getQuestionId());
            if (question == null) {
                continue;
            }

            QuestionAnalysisVO vo = new QuestionAnalysisVO();
            vo.setQuestionId(question.getId());
            vo.setContent(question.getContent());
            vo.setQuestionType(question.getQuestionType());
            vo.setFullScore(pq.getScore());
            vo.setTotalCount(totalStudents);

            // 查询该题所有答案
            List<Long> recordIds = records.stream().map(ExamRecord::getId).collect(Collectors.toList());
            if (recordIds.isEmpty()) {
                vo.setCorrectCount(0);
                vo.setCorrectRate(BigDecimal.ZERO);
                vo.setAvgScore(BigDecimal.ZERO);
                vo.setDiscrimination(BigDecimal.ZERO);
                analysisList.add(vo);
                continue;
            }

            LambdaQueryWrapper<ExamAnswer> answerWrapper = new LambdaQueryWrapper<>();
            answerWrapper.eq(ExamAnswer::getQuestionId, question.getId())
                    .in(ExamAnswer::getRecordId, recordIds);
            List<ExamAnswer> answers = examAnswerMapper.selectList(answerWrapper);

            int correctCount = 0;
            BigDecimal totalQuestionScore = BigDecimal.ZERO;

            for (ExamAnswer answer : answers) {
                if (answer.getIsCorrect() != null && answer.getIsCorrect() == 1) {
                    correctCount++;
                }
                if (answer.getScore() != null) {
                    totalQuestionScore = totalQuestionScore.add(answer.getScore());
                }
            }

            vo.setCorrectCount(correctCount);
            BigDecimal correctRate = totalStudents > 0
                    ? BigDecimal.valueOf(correctCount)
                    .divide(BigDecimal.valueOf(totalStudents), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
            vo.setCorrectRate(correctRate);

            BigDecimal avgScore = answers.size() > 0
                    ? totalQuestionScore.divide(BigDecimal.valueOf(answers.size()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            vo.setAvgScore(avgScore);

            // 计算区分度：高分组正确率 - 低分组正确率
            vo.setDiscrimination(calculateDiscrimination(records, question.getId(), pq.getScore()));

            analysisList.add(vo);
        }

        return analysisList;
    }

    /**
     * 计算题目区分度（高分组正确率 - 低分组正确率）
     */
    private BigDecimal calculateDiscrimination(List<ExamRecord> records, Long questionId, BigDecimal fullScore) {
        if (records.size() < 2) {
            return BigDecimal.ZERO;
        }

        // 按总分排序
        List<ExamRecord> sorted = records.stream()
                .sorted(Comparator.comparing(r -> r.getTotalScore() != null ? r.getTotalScore() : BigDecimal.ZERO))
                .collect(Collectors.toList());

        int size = sorted.size();
        // 取前27%为低分组，后27%为高分组
        int groupSize = Math.max(1, (int) Math.ceil(size * 0.27));

        List<ExamRecord> lowGroup = sorted.subList(0, groupSize);
        List<ExamRecord> highGroup = sorted.subList(size - groupSize, size);

        // 查询两组的答案
        List<Long> lowIds = lowGroup.stream().map(ExamRecord::getId).collect(Collectors.toList());
        List<Long> highIds = highGroup.stream().map(ExamRecord::getId).collect(Collectors.toList());

        BigDecimal lowRate = getCorrectRate(questionId, lowIds);
        BigDecimal highRate = getCorrectRate(questionId, highIds);

        return highRate.subtract(lowRate).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal getCorrectRate(Long questionId, List<Long> recordIds) {
        if (recordIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        LambdaQueryWrapper<ExamAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamAnswer::getQuestionId, questionId)
                .in(ExamAnswer::getRecordId, recordIds);
        List<ExamAnswer> answers = examAnswerMapper.selectList(wrapper);

        if (answers.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long correctCount = answers.stream()
                .filter(a -> a.getIsCorrect() != null && a.getIsCorrect() == 1)
                .count();
        return BigDecimal.valueOf(correctCount)
                .divide(BigDecimal.valueOf(answers.size()), 4, RoundingMode.HALF_UP);
    }

    @Override
    public List<StudentTrendVO> getStudentTrend(Long studentId, Long subjectId) {
        // 查询该学生参加的所有测评记录
        LambdaQueryWrapper<ExamRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(ExamRecord::getStudentId, studentId)
                .eq(ExamRecord::getStatus, 1)
                .orderByAsc(ExamRecord::getSubmitTime);
        List<ExamRecord> records = examRecordMapper.selectList(recordWrapper);

        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        List<StudentTrendVO> trendList = new ArrayList<>();
        int rank = 1;

        for (ExamRecord record : records) {
            Exam exam = examMapper.selectById(record.getExamId());
            if (exam == null) {
                continue;
            }

            // 如果指定了学科，需要通过试卷过滤
            if (subjectId != null) {
                Paper paper = paperMapper.selectById(exam.getPaperId());
                if (paper == null || !subjectId.equals(paper.getSubjectId())) {
                    continue;
                }
            }

            StudentTrendVO vo = new StudentTrendVO();
            vo.setExamId(exam.getId());
            vo.setExamName(exam.getExamName());
            vo.setScore(record.getTotalScore());
            vo.setRank(record.getRankInClass());
            vo.setExamTime(record.getSubmitTime() != null ? record.getSubmitTime().toString() : "");
            trendList.add(vo);
        }

        return trendList;
    }

    @Override
    public List<ClassRankingVO> getClassRanking(Long classId, Long examId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getClassId, classId)
                .eq(ExamRecord::getStatus, 1)
                .orderByDesc(ExamRecord::getTotalScore);
        List<ExamRecord> records = examRecordMapper.selectList(wrapper);

        List<ClassRankingVO> rankingList = new ArrayList<>();
        int rank = 1;
        for (ExamRecord record : records) {
            ClassRankingVO vo = new ClassRankingVO();
            vo.setStudentId(record.getStudentId());
            vo.setClassId(classId);
            vo.setScore(record.getTotalScore());
            vo.setRank(rank);
            vo.setTimeUsed(record.getTimeUsed());
            rankingList.add(vo);
            rank++;
        }

        return rankingList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importOfflineScores(Long examId, MultipartFile file) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("测评不存在");
        }

        Paper paper = paperMapper.selectById(exam.getPaperId());
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException("Excel文件为空");
            }

            // 跳过表头，从第二行开始读取
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // 假设Excel格式：学生ID | 分数
                Long studentId = null;
                BigDecimal score = null;

                try {
                    studentId = (long) row.getCell(0).getNumericCellValue();
                    score = BigDecimal.valueOf(row.getCell(1).getNumericCellValue());
                } catch (Exception e) {
                    // 跳过格式错误的行
                    continue;
                }

                if (studentId == null || score == null) {
                    continue;
                }

                // 检查是否已有记录
                LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ExamRecord::getExamId, examId)
                        .eq(ExamRecord::getStudentId, studentId);
                ExamRecord existing = examRecordMapper.selectOne(wrapper);

                if (existing != null) {
                    existing.setTotalScore(score);
                    existing.setObjectiveScore(score); // 线下导入默认全部为客观分
                    existing.setSubjectiveScore(BigDecimal.ZERO);
                    existing.setStatus(1); // 已交卷
                    existing.setSubmitTime(LocalDateTime.now());
                    existing.setUpdatedAt(LocalDateTime.now());
                    examRecordMapper.updateById(existing);
                } else {
                    ExamRecord record = new ExamRecord();
                    record.setTenantId(exam.getTenantId());
                    record.setExamId(examId);
                    record.setStudentId(studentId);
                    record.setTotalScore(score);
                    record.setObjectiveScore(score);
                    record.setSubjectiveScore(BigDecimal.ZERO);
                    record.setStatus(1); // 已交卷
                    record.setSubmitTime(LocalDateTime.now());
                    record.setCreatedAt(LocalDateTime.now());
                    record.setUpdatedAt(LocalDateTime.now());
                    examRecordMapper.insert(record);
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Excel文件解析失败：" + e.getMessage());
        }
    }
}
