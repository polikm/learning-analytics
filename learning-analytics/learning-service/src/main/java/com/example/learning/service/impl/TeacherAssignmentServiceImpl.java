package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.TeacherAssignment;
import com.example.learning.model.mapper.TeacherAssignmentMapper;
import com.example.learning.service.TeacherAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师任课分配服务实现类
 */
@Service
public class TeacherAssignmentServiceImpl implements TeacherAssignmentService {

    @Autowired
    private TeacherAssignmentMapper teacherAssignmentMapper;

    @Override
    public List<TeacherAssignment> getList(Long teacherId, Long schoolId, Long subjectId, String academicYear) {
        return teacherAssignmentMapper.selectList(
                new LambdaQueryWrapper<TeacherAssignment>()
                        .eq(teacherId != null, TeacherAssignment::getTeacherId, teacherId)
                        .eq(schoolId != null, TeacherAssignment::getSchoolId, schoolId)
                        .eq(subjectId != null, TeacherAssignment::getSubjectId, subjectId)
                        .eq(StringUtils.hasText(academicYear), TeacherAssignment::getAcademicYear, academicYear)
                        .orderByDesc(TeacherAssignment::getCreatedAt)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeacherAssignment create(TeacherAssignment assignment) {
        assignment.setStatus(1);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        teacherAssignmentMapper.insert(assignment);
        return assignment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeacherAssignment update(Long id, TeacherAssignment assignment) {
        TeacherAssignment existing = teacherAssignmentMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (assignment.getSchoolId() != null) {
            existing.setSchoolId(assignment.getSchoolId());
        }
        if (assignment.getGradeId() != null) {
            existing.setGradeId(assignment.getGradeId());
        }
        if (assignment.getClassId() != null) {
            existing.setClassId(assignment.getClassId());
        }
        if (assignment.getSubjectId() != null) {
            existing.setSubjectId(assignment.getSubjectId());
        }
        if (assignment.getAcademicYear() != null) {
            existing.setAcademicYear(assignment.getAcademicYear());
        }
        if (assignment.getSemester() != null) {
            existing.setSemester(assignment.getSemester());
        }
        if (assignment.getStatus() != null) {
            existing.setStatus(assignment.getStatus());
        }
        if (assignment.getRemark() != null) {
            existing.setRemark(assignment.getRemark());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        teacherAssignmentMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        TeacherAssignment existing = teacherAssignmentMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        teacherAssignmentMapper.deleteById(id);
    }

    @Override
    public List<TeacherAssignment> getByTeacher(Long teacherId) {
        return teacherAssignmentMapper.selectList(
                new LambdaQueryWrapper<TeacherAssignment>()
                        .eq(TeacherAssignment::getTeacherId, teacherId)
                        .orderByDesc(TeacherAssignment::getCreatedAt)
        );
    }
}
