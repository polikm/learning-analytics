package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.Subject;
import com.example.learning.model.mapper.SubjectMapper;
import com.example.learning.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学科服务实现类
 */
@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectMapper subjectMapper;

    @Override
    public List<Subject> getList(Long tenantId) {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(Subject::getTenantId, tenantId);
        }
        wrapper.orderByAsc(Subject::getSortOrder);
        return subjectMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Subject create(Subject subject) {
        subject.setStatus(1);
        subject.setCreatedAt(LocalDateTime.now());
        subjectMapper.insert(subject);
        return subject;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Subject update(Long id, Subject subject) {
        Subject existing = subjectMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        subject.setId(id);
        subjectMapper.updateById(subject);
        return subjectMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Subject existing = subjectMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        subjectMapper.deleteById(id);
    }
}
