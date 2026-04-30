package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Exam;
import com.example.learning.model.entity.ExamRecord;
import com.example.learning.model.mapper.ExamMapper;
import com.example.learning.model.mapper.ExamRecordMapper;
import com.example.learning.service.ExamRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 测评记录服务实现类
 */
@Service
public class ExamRecordServiceImpl implements ExamRecordService {

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private ExamMapper examMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecord startExam(Long examId, Long studentId) {
        // 检查测评是否存在
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查是否已存在答题记录
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStudentId, studentId);
        ExamRecord existing = examRecordMapper.selectOne(wrapper);
        if (existing != null) {
            return existing;
        }

        // 创建答题记录
        ExamRecord record = new ExamRecord();
        record.setTenantId(exam.getTenantId());
        record.setExamId(examId);
        record.setStudentId(studentId);
        record.setStatus(0); // 答题中
        record.setStartTime(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        examRecordMapper.insert(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitExam(Long examId, Long studentId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStudentId, studentId)
                .eq(ExamRecord::getStatus, 0); // 答题中
        ExamRecord record = examRecordMapper.selectOne(wrapper);
        if (record == null) {
            throw new BusinessException("未找到答题记录或已交卷");
        }

        // 计算答题时长
        if (record.getStartTime() != null) {
            Duration duration = Duration.between(record.getStartTime(), LocalDateTime.now());
            record.setTimeUsed((int) duration.getSeconds());
        }

        record.setStatus(1); // 已交卷
        record.setSubmitTime(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        examRecordMapper.updateById(record);
    }

    @Override
    public ExamRecord getByExamAndStudent(Long examId, Long studentId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
                .eq(ExamRecord::getStudentId, studentId);
        return examRecordMapper.selectOne(wrapper);
    }

    @Override
    public Page<ExamRecord> getPageByExam(Long examId, PageQueryDTO dto) {
        Page<ExamRecord> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getExamId, examId)
                .orderByDesc(ExamRecord::getTotalScore);
        return examRecordMapper.selectPage(page, wrapper);
    }
}
