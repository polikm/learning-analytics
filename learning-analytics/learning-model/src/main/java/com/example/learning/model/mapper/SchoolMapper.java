package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.School;
import org.apache.ibatis.annotations.Mapper;

/**
 * School Mapper 接口
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {
}
