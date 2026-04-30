package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Question;
import com.example.learning.model.mapper.QuestionMapper;
import com.example.learning.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 题目服务实现类
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Page<Question> getPage(PageQueryDTO dto, Long subjectId, Integer questionType,
                                  Integer difficulty, Long knowledgeId, Integer reviewStatus, String keyword) {
        Page<Question> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        if (subjectId != null) {
            wrapper.eq(Question::getSubjectId, subjectId);
        }
        if (questionType != null) {
            wrapper.eq(Question::getQuestionType, questionType);
        }
        if (difficulty != null) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (knowledgeId != null) {
            // 模糊匹配 knowledgeIds 字段（包含该知识点的题目）
            wrapper.like(Question::getKnowledgeIds, String.valueOf(knowledgeId));
        }
        if (reviewStatus != null) {
            wrapper.eq(Question::getReviewStatus, reviewStatus);
        }
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Question::getContent, keyword)
                    .or().like(Question::getTags, keyword));
        }

        wrapper.orderByDesc(Question::getCreatedAt);
        return questionMapper.selectPage(page, wrapper);
    }

    @Override
    public Question getById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Question create(Question question) {
        question.setStatus(1);
        question.setReviewStatus(0); // 待审核
        question.setUsageCount(0);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        questionMapper.insert(question);
        return question;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Question update(Long id, Question question) {
        Question existing = questionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        question.setId(id);
        question.setUpdatedAt(LocalDateTime.now());
        questionMapper.updateById(question);
        return questionMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Question existing = questionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        questionMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(Long id, Integer reviewStatus) {
        Question existing = questionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        Question update = new Question();
        update.setId(id);
        update.setReviewStatus(reviewStatus);
        update.setUpdatedAt(LocalDateTime.now());
        questionMapper.updateById(update);
    }
}
