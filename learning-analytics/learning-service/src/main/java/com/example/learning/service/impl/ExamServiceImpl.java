package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Exam;
import com.example.learning.model.mapper.ExamMapper;
import com.example.learning.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 测评服务实现类
 */
@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public Page<Exam> getPage(PageQueryDTO dto) {
        Page<Exam> page = new Page<>(dto.getPage(), dto.getSize());
        return examMapper.selectPage(page, new LambdaQueryWrapper<Exam>()
                .orderByDesc(Exam::getCreatedAt));
    }

    @Override
    public Exam getById(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return exam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Exam create(Exam exam) {
        exam.setStatus(0); // 草稿
        exam.setCreatedAt(LocalDateTime.now());
        exam.setUpdatedAt(LocalDateTime.now());
        examMapper.insert(exam);
        return exam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Exam update(Long id, Exam exam) {
        Exam existing = examMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的测评才能修改");
        }
        exam.setId(id);
        exam.setUpdatedAt(LocalDateTime.now());
        examMapper.updateById(exam);
        return examMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Exam existing = examMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的测评才能删除");
        }
        examMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        Exam existing = examMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (existing.getStatus() != 0) {
            throw new BusinessException("只有草稿状态的测评才能发布");
        }
        Exam update = new Exam();
        update.setId(id);
        update.setStatus(1); // 进行中
        update.setUpdatedAt(LocalDateTime.now());
        examMapper.updateById(update);
    }
}
