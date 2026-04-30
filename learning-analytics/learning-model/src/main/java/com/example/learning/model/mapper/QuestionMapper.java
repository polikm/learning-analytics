package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * Question Mapper 接口
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
