package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * StudentProfile Mapper 接口
 */
@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {
}
