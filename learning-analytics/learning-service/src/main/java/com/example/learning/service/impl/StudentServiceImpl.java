package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.StudentProfile;
import com.example.learning.model.mapper.StudentProfileMapper;
import com.example.learning.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentProfileMapper studentProfileMapper;

    @Override
    public Page<StudentProfile> getPage(PageQueryDTO dto) {
        Page<StudentProfile> page = new Page<>(dto.getPage(), dto.getSize());
        return studentProfileMapper.selectPage(page, new LambdaQueryWrapper<StudentProfile>()
                .orderByDesc(StudentProfile::getCreatedAt));
    }

    @Override
    public StudentProfile getById(Long id) {
        StudentProfile student = studentProfileMapper.selectById(id);
        if (student == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return student;
    }

    @Override
    public List<StudentProfile> getByClassId(Long classId) {
        return studentProfileMapper.selectList(
                new LambdaQueryWrapper<StudentProfile>()
                        .eq(StudentProfile::getClassId, classId)
                        .orderByAsc(StudentProfile::getStudentNo)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentProfile create(StudentProfile studentProfile) {
        studentProfile.setCreatedAt(LocalDateTime.now());
        studentProfile.setUpdatedAt(LocalDateTime.now());
        studentProfileMapper.insert(studentProfile);
        return studentProfile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentProfile update(Long id, StudentProfile studentProfile) {
        StudentProfile existing = studentProfileMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (studentProfile.getUserId() != null) {
            existing.setUserId(studentProfile.getUserId());
        }
        if (studentProfile.getSchoolId() != null) {
            existing.setSchoolId(studentProfile.getSchoolId());
        }
        if (studentProfile.getGradeId() != null) {
            existing.setGradeId(studentProfile.getGradeId());
        }
        if (studentProfile.getClassId() != null) {
            existing.setClassId(studentProfile.getClassId());
        }
        if (studentProfile.getStudentNo() != null) {
            existing.setStudentNo(studentProfile.getStudentNo());
        }
        if (studentProfile.getGender() != null) {
            existing.setGender(studentProfile.getGender());
        }
        if (studentProfile.getBirthDate() != null) {
            existing.setBirthDate(studentProfile.getBirthDate());
        }
        if (studentProfile.getEnrollmentDate() != null) {
            existing.setEnrollmentDate(studentProfile.getEnrollmentDate());
        }
        if (studentProfile.getAcademicStatus() != null) {
            existing.setAcademicStatus(studentProfile.getAcademicStatus());
        }
        if (studentProfile.getParentPhone() != null) {
            existing.setParentPhone(studentProfile.getParentPhone());
        }
        if (studentProfile.getExtraInfo() != null) {
            existing.setExtraInfo(studentProfile.getExtraInfo());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        studentProfileMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentProfile existing = studentProfileMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        studentProfileMapper.deleteById(id);
    }
}
