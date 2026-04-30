package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.Certificate;
import org.apache.ibatis.annotations.Mapper;

/**
 * Certificate Mapper 接口
 */
@Mapper
public interface CertificateMapper extends BaseMapper<Certificate> {
}
