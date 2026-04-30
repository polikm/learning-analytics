package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.Report;
import org.apache.ibatis.annotations.Mapper;

/**
 * Report Mapper 接口
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {
}
