package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.Grade;
import com.example.learning.model.mapper.GradeMapper;
import com.example.learning.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 年级服务实现类
 */
@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeMapper gradeMapper;

    @Override
    public List<Grade> getList(Long schoolId) {
        return gradeMapper.selectList(
                new LambdaQueryWrapper<Grade>()
                        .eq(Grade::getSchoolId, schoolId)
                        .orderByAsc(Grade::getGradeLevel)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Grade create(Grade grade) {
        grade.setStatus(1);
        grade.setCreatedAt(LocalDateTime.now());
        gradeMapper.insert(grade);
        return grade;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Grade update(Long id, Grade grade) {
        Grade existing = gradeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (grade.getGradeName() != null) {
            existing.setGradeName(grade.getGradeName());
        }
        if (grade.getGradeLevel() != null) {
            existing.setGradeLevel(grade.getGradeLevel());
        }
        if (grade.getAcademicYear() != null) {
            existing.setAcademicYear(grade.getAcademicYear());
        }
        if (grade.getStatus() != null) {
            existing.setStatus(grade.getStatus());
        }

        gradeMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Grade existing = gradeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        gradeMapper.deleteById(id);
    }
}
