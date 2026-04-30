package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Paper;
import com.example.learning.model.entity.PaperQuestion;
import com.example.learning.model.entity.Question;
import com.example.learning.model.mapper.PaperMapper;
import com.example.learning.model.mapper.PaperQuestionMapper;
import com.example.learning.model.mapper.QuestionMapper;
import com.example.learning.model.vo.PaperDetailVO;
import com.example.learning.model.vo.PaperQuestionVO;
import com.example.learning.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 试卷服务实现类
 */
@Service
public class PaperServiceImpl implements PaperService {

    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Page<Paper> getPage(PageQueryDTO dto) {
        Page<Paper> page = new Page<>(dto.getPage(), dto.getSize());
        return paperMapper.selectPage(page, new LambdaQueryWrapper<Paper>()
                .orderByDesc(Paper::getCreatedAt));
    }

    @Override
    public PaperDetailVO getById(Long id) {
        Paper paper = paperMapper.selectById(id);
        if (paper == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        PaperDetailVO vo = new PaperDetailVO();
        vo.setId(paper.getId());
        vo.setPaperName(paper.getPaperName());
        vo.setSubjectId(paper.getSubjectId());
        vo.setGradeLevel(paper.getGradeLevel());
        vo.setTotalScore(paper.getTotalScore());
        vo.setQuestionCount(paper.getQuestionCount());
        vo.setDifficultyAvg(paper.getDifficultyAvg());
        vo.setDuration(paper.getDuration());
        vo.setPaperType(paper.getPaperType());
        vo.setPassScore(paper.getPassScore());

        // 查询试卷关联的题目
        LambdaQueryWrapper<PaperQuestion> pqWrapper = new LambdaQueryWrapper<>();
        pqWrapper.eq(PaperQuestion::getPaperId, id)
                .orderByAsc(PaperQuestion::getSectionIndex)
                .orderByAsc(PaperQuestion::getSortOrder);
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(pqWrapper);

        List<PaperQuestionVO> questionVOList = paperQuestions.stream().map(pq -> {
            PaperQuestionVO pqVO = new PaperQuestionVO();
            pqVO.setId(pq.getId());
            pqVO.setQuestionId(pq.getQuestionId());
            pqVO.setSectionIndex(pq.getSectionIndex());
            pqVO.setQuestionSeq(pq.getQuestionSeq());
            pqVO.setScore(pq.getScore());
            pqVO.setSortOrder(pq.getSortOrder());
            // 查询题目详情
            Question question = questionMapper.selectById(pq.getQuestionId());
            pqVO.setQuestion(question);
            return pqVO;
        }).collect(Collectors.toList());

        vo.setQuestions(questionVOList);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Paper create(Paper paper, List<PaperQuestion> questions) {
        paper.setStatus(1);
        paper.setCreatedAt(LocalDateTime.now());
        paper.setUpdatedAt(LocalDateTime.now());

        // 计算总分和题目数量
        BigDecimal totalScore = BigDecimal.ZERO;
        int questionCount = questions != null ? questions.size() : 0;
        if (questions != null) {
            for (PaperQuestion pq : questions) {
                if (pq.getScore() != null) {
                    totalScore = totalScore.add(pq.getScore());
                }
            }
        }
        paper.setTotalScore(totalScore);
        paper.setQuestionCount(questionCount);

        paperMapper.insert(paper);

        // 保存试卷题目关联
        if (questions != null && !questions.isEmpty()) {
            for (PaperQuestion pq : questions) {
                pq.setPaperId(paper.getId());
                paperQuestionMapper.insert(pq);
            }
        }

        return paper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Paper update(Long id, Paper paper, List<PaperQuestion> questions) {
        Paper existing = paperMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        paper.setId(id);
        paper.setUpdatedAt(LocalDateTime.now());

        // 重新计算总分和题目数量
        BigDecimal totalScore = BigDecimal.ZERO;
        int questionCount = questions != null ? questions.size() : 0;
        if (questions != null) {
            for (PaperQuestion pq : questions) {
                if (pq.getScore() != null) {
                    totalScore = totalScore.add(pq.getScore());
                }
            }
        }
        paper.setTotalScore(totalScore);
        paper.setQuestionCount(questionCount);

        paperMapper.updateById(paper);

        // 删除旧的题目关联
        LambdaQueryWrapper<PaperQuestion> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(PaperQuestion::getPaperId, id);
        paperQuestionMapper.delete(deleteWrapper);

        // 保存新的题目关联
        if (questions != null && !questions.isEmpty()) {
            for (PaperQuestion pq : questions) {
                pq.setPaperId(id);
                pq.setId(null); // 重置ID，让数据库自动生成
                paperQuestionMapper.insert(pq);
            }
        }

        return paperMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Paper existing = paperMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 删除试卷题目关联
        LambdaQueryWrapper<PaperQuestion> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(PaperQuestion::getPaperId, id);
        paperQuestionMapper.delete(deleteWrapper);
        // 删除试卷
        paperMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Paper autoGenerate(Long subjectId, String knowledgeIds, Integer difficulty,
                              Map<Integer, Integer> typeRatio, Integer totalCount) {
        // 1. 根据条件筛选题库中的题目
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getSubjectId, subjectId)
                .eq(Question::getStatus, 1)
                .eq(Question::getReviewStatus, 1); // 已审核通过

        if (difficulty != null) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (knowledgeIds != null && !knowledgeIds.isEmpty()) {
            // 知识点筛选：题目knowledgeIds包含任意一个指定知识点
            String[] kpIdArray = knowledgeIds.split(",");
            wrapper.and(w -> {
                for (String kpId : kpIdArray) {
                    w.or().like(Question::getKnowledgeIds, kpId.trim());
                }
            });
        }

        List<Question> allQuestions = questionMapper.selectList(wrapper);

        if (allQuestions.isEmpty()) {
            throw new BusinessException("题库中没有符合条件的题目");
        }

        // 2. 按题型分组
        Map<String, List<Question>> questionsByType = allQuestions.stream()
                .collect(Collectors.groupingBy(Question::getQuestionType));

        // 3. 根据题型比例随机抽取题目
        List<PaperQuestion> selectedQuestions = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        int seq = 1;

        if (typeRatio != null && !typeRatio.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : typeRatio.entrySet()) {
                String questionType = String.valueOf(entry.getKey());
                int count = entry.getValue();

                List<Question> typeQuestions = questionsByType.get(questionType);
                if (typeQuestions == null || typeQuestions.isEmpty()) {
                    throw new BusinessException("题库中题型 [" + questionType + "] 没有符合条件的题目");
                }

                // 随机抽取指定数量
                List<Question> selected = randomSelect(typeQuestions, count);
                for (Question q : selected) {
                    PaperQuestion pq = new PaperQuestion();
                    pq.setQuestionId(q.getId());
                    pq.setSectionIndex(1);
                    pq.setQuestionSeq(seq++);
                    pq.setScore(q.getScore() != null ? q.getScore() : BigDecimal.valueOf(5));
                    pq.setSortOrder(seq - 1);
                    selectedQuestions.add(pq);
                    totalScore = totalScore.add(pq.getScore());
                }
            }
        } else {
            // 没有指定题型比例，直接随机抽取totalCount道题
            List<Question> selected = randomSelect(allQuestions, totalCount);
            for (Question q : selected) {
                PaperQuestion pq = new PaperQuestion();
                pq.setQuestionId(q.getId());
                pq.setSectionIndex(1);
                pq.setQuestionSeq(seq++);
                pq.setScore(q.getScore() != null ? q.getScore() : BigDecimal.valueOf(5));
                pq.setSortOrder(seq - 1);
                selectedQuestions.add(pq);
                totalScore = totalScore.add(pq.getScore());
            }
        }

        // 4. 计算平均难度
        double avgDifficulty = selectedQuestions.stream()
                .mapToDouble(pq -> {
                    Question q = questionMapper.selectById(pq.getQuestionId());
                    return q != null && q.getDifficulty() != null ? q.getDifficulty() : 3;
                })
                .average()
                .orElse(3.0);

        // 5. 创建试卷
        Paper paper = new Paper();
        paper.setSubjectId(subjectId);
        paper.setPaperName("智能组卷_" + System.currentTimeMillis());
        paper.setTotalScore(totalScore);
        paper.setQuestionCount(selectedQuestions.size());
        paper.setDifficultyAvg(BigDecimal.valueOf(avgDifficulty).setScale(1, RoundingMode.HALF_UP));
        paper.setDuration(120);
        paper.setPaperType("auto");
        paper.setPassScore(totalScore.multiply(BigDecimal.valueOf(0.6)).setScale(0, RoundingMode.HALF_UP));
        paper.setStatus(1);
        paper.setCreatedAt(LocalDateTime.now());
        paper.setUpdatedAt(LocalDateTime.now());

        paperMapper.insert(paper);

        // 6. 保存试卷题目关联
        for (PaperQuestion pq : selectedQuestions) {
            pq.setPaperId(paper.getId());
            paperQuestionMapper.insert(pq);
        }

        return paper;
    }

    /**
     * 从列表中随机抽取指定数量的元素
     *
     * @param list   源列表
     * @param count  抽取数量
     * @param <T>    元素类型
     * @return 抽取结果
     */
    private <T> List<T> randomSelect(List<T> list, int count) {
        if (list.size() <= count) {
            return new ArrayList<>(list);
        }
        Collections.shuffle(list);
        return list.subList(0, count);
    }
}
