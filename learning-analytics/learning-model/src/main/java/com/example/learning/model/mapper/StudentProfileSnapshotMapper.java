package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.StudentProfileSnapshot;
import org.apache.ibatis.annotations.Mapper;

/**
 * StudentProfileSnapshot Mapper 接口
 */
@Mapper
public interface StudentProfileSnapshotMapper extends BaseMapper<StudentProfileSnapshot> {
}
