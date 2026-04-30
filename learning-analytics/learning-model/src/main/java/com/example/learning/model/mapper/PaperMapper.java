package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.Paper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Paper Mapper 接口
 */
@Mapper
public interface PaperMapper extends BaseMapper<Paper> {
}
