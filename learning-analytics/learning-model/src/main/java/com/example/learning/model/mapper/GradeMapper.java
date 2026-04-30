package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.Grade;
import org.apache.ibatis.annotations.Mapper;

/**
 * Grade Mapper 接口
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
}
