package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.KnowledgePoint;
import org.apache.ibatis.annotations.Mapper;

/**
 * KnowledgePoint Mapper 接口
 */
@Mapper
public interface KnowledgePointMapper extends BaseMapper<KnowledgePoint> {
}
