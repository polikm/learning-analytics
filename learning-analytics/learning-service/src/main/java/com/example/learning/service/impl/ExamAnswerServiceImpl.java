package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.model.entity.ExamAnswer;
import com.example.learning.model.mapper.ExamAnswerMapper;
import com.example.learning.service.ExamAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 测评答案服务实现类
 */
@Service
public class ExamAnswerServiceImpl implements ExamAnswerService {

    @Autowired
    private ExamAnswerMapper examAnswerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAnswer(Long recordId, Long questionId, String studentAnswer) {
        // 查询是否已有该题目的答案记录
        LambdaQueryWrapper<ExamAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamAnswer::getRecordId, recordId)
                .eq(ExamAnswer::getQuestionId, questionId);
        ExamAnswer existing = examAnswerMapper.selectOne(wrapper);

        if (existing != null) {
            // 更新已有答案
            existing.setStudentAnswer(studentAnswer);
            existing.setAnswerTime(LocalDateTime.now());
            examAnswerMapper.updateById(existing);
        } else {
            // 新增答案
            ExamAnswer answer = new ExamAnswer();
            answer.setRecordId(recordId);
            answer.setQuestionId(questionId);
            answer.setStudentAnswer(studentAnswer);
            answer.setAnswerTime(LocalDateTime.now());
            answer.setCreatedAt(LocalDateTime.now());
            examAnswerMapper.insert(answer);
        }
    }

    @Override
    public List<ExamAnswer> getAnswersByRecord(Long recordId) {
        LambdaQueryWrapper<ExamAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamAnswer::getRecordId, recordId)
                .orderByAsc(ExamAnswer::getId);
        return examAnswerMapper.selectList(wrapper);
    }
}
