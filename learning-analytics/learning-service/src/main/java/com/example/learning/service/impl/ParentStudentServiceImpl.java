package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.ParentStudent;
import com.example.learning.model.mapper.ParentStudentMapper;
import com.example.learning.service.ParentStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 家长学生关联服务实现类
 */
@Service
public class ParentStudentServiceImpl implements ParentStudentService {

    @Autowired
    private ParentStudentMapper parentStudentMapper;

    @Override
    public List<ParentStudent> getList(Long parentId, Long studentId) {
        return parentStudentMapper.selectList(
                new LambdaQueryWrapper<ParentStudent>()
                        .eq(parentId != null, ParentStudent::getParentId, parentId)
                        .eq(studentId != null, ParentStudent::getStudentId, studentId)
                        .orderByDesc(ParentStudent::getCreatedAt)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParentStudent create(ParentStudent ps) {
        ps.setStatus(1);
        ps.setCreatedAt(LocalDateTime.now());
        ps.setUpdatedAt(LocalDateTime.now());
        parentStudentMapper.insert(ps);
        return ps;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParentStudent update(Long id, ParentStudent ps) {
        ParentStudent existing = parentStudentMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (ps.getRelation() != null) {
            existing.setRelation(ps.getRelation());
        }
        if (ps.getStatus() != null) {
            existing.setStatus(ps.getStatus());
        }
        if (ps.getRemark() != null) {
            existing.setRemark(ps.getRemark());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        parentStudentMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ParentStudent existing = parentStudentMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        parentStudentMapper.deleteById(id);
    }

    @Override
    public List<ParentStudent> getByParent(Long parentId) {
        return parentStudentMapper.selectList(
                new LambdaQueryWrapper<ParentStudent>()
                        .eq(ParentStudent::getParentId, parentId)
                        .orderByDesc(ParentStudent::getCreatedAt)
        );
    }

    @Override
    public List<ParentStudent> getByStudent(Long studentId) {
        return parentStudentMapper.selectList(
                new LambdaQueryWrapper<ParentStudent>()
                        .eq(ParentStudent::getStudentId, studentId)
                        .orderByDesc(ParentStudent::getCreatedAt)
        );
    }
}
