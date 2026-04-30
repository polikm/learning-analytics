package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.Exam;
import org.apache.ibatis.annotations.Mapper;

/**
 * Exam Mapper 接口
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
}
