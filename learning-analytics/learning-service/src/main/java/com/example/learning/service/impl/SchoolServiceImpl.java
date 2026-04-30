package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.dto.SchoolDTO;
import com.example.learning.model.entity.School;
import com.example.learning.model.mapper.SchoolMapper;
import com.example.learning.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 学校服务实现类
 */
@Service
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private SchoolMapper schoolMapper;

    @Override
    public Page<School> getPage(PageQueryDTO dto) {
        Page<School> page = new Page<>(dto.getPage(), dto.getSize());
        return schoolMapper.selectPage(page, new LambdaQueryWrapper<School>()
                .orderByDesc(School::getCreatedAt));
    }

    @Override
    public School getById(Long id) {
        School school = schoolMapper.selectById(id);
        if (school == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return school;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public School create(SchoolDTO dto) {
        School school = new School();
        school.setSchoolCode(dto.getSchoolCode());
        school.setSchoolName(dto.getSchoolName());
        school.setSchoolType(dto.getSchoolType());
        school.setProvince(dto.getProvince());
        school.setCity(dto.getCity());
        school.setDistrict(dto.getDistrict());
        school.setAddress(dto.getAddress());
        school.setStatus(1);
        school.setCreatedAt(LocalDateTime.now());
        school.setUpdatedAt(LocalDateTime.now());

        schoolMapper.insert(school);
        return school;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public School update(Long id, SchoolDTO dto) {
        School school = schoolMapper.selectById(id);
        if (school == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (dto.getSchoolCode() != null) {
            school.setSchoolCode(dto.getSchoolCode());
        }
        if (dto.getSchoolName() != null) {
            school.setSchoolName(dto.getSchoolName());
        }
        if (dto.getSchoolType() != null) {
            school.setSchoolType(dto.getSchoolType());
        }
        if (dto.getProvince() != null) {
            school.setProvince(dto.getProvince());
        }
        if (dto.getCity() != null) {
            school.setCity(dto.getCity());
        }
        if (dto.getDistrict() != null) {
            school.setDistrict(dto.getDistrict());
        }
        if (dto.getAddress() != null) {
            school.setAddress(dto.getAddress());
        }
        school.setUpdatedAt(LocalDateTime.now());

        schoolMapper.updateById(school);
        return school;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        School school = schoolMapper.selectById(id);
        if (school == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        schoolMapper.deleteById(id);
    }
}
