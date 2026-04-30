package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.Class;
import com.example.learning.model.mapper.ClassMapper;
import com.example.learning.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级服务实现类
 */
@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Override
    public List<Class> getList(Long gradeId) {
        return classMapper.selectList(
                new LambdaQueryWrapper<Class>()
                        .eq(Class::getGradeId, gradeId)
                        .orderByAsc(Class::getClassNo)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Class create(Class clazz) {
        clazz.setStatus(1);
        clazz.setCreatedAt(LocalDateTime.now());
        classMapper.insert(clazz);
        return clazz;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Class update(Long id, Class clazz) {
        Class existing = classMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (clazz.getClassName() != null) {
            existing.setClassName(clazz.getClassName());
        }
        if (clazz.getClassNo() != null) {
            existing.setClassNo(clazz.getClassNo());
        }
        if (clazz.getAdvisorId() != null) {
            existing.setAdvisorId(clazz.getAdvisorId());
        }
        if (clazz.getMaxStudents() != null) {
            existing.setMaxStudents(clazz.getMaxStudents());
        }
        if (clazz.getStatus() != null) {
            existing.setStatus(clazz.getStatus());
        }

        classMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Class existing = classMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        classMapper.deleteById(id);
    }
}
