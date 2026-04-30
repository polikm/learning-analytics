package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Certificate;
import com.example.learning.model.mapper.CertificateMapper;
import com.example.learning.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 证书管理服务实现类
 */
@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateMapper certificateMapper;

    @Override
    public Page<Certificate> getPage(PageQueryDTO dto, Long studentId, String certType) {
        Page<Certificate> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) {
            wrapper.eq(Certificate::getStudentId, studentId);
        }
        if (StringUtils.hasText(certType)) {
            wrapper.eq(Certificate::getCertType, certType);
        }
        wrapper.orderByDesc(Certificate::getCreatedAt);
        return certificateMapper.selectPage(page, wrapper);
    }

    @Override
    public Certificate getById(Long id) {
        Certificate certificate = certificateMapper.selectById(id);
        if (certificate == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return certificate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Certificate create(Certificate certificate) {
        certificate.setReviewStatus(0); // 待审核
        certificate.setCreatedAt(LocalDateTime.now());
        certificate.setUpdatedAt(LocalDateTime.now());
        certificateMapper.insert(certificate);
        return certificate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Certificate update(Long id, Certificate certificate) {
        Certificate existing = certificateMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        certificate.setId(id);
        certificate.setUpdatedAt(LocalDateTime.now());
        certificateMapper.updateById(certificate);
        return certificateMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Certificate existing = certificateMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        certificateMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(Long id, Integer reviewStatus, String reviewRemark) {
        Certificate existing = certificateMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        Certificate update = new Certificate();
        update.setId(id);
        update.setReviewStatus(reviewStatus);
        update.setReviewRemark(reviewRemark);
        update.setUpdatedAt(LocalDateTime.now());
        certificateMapper.updateById(update);
    }

    @Override
    public Map<String, Object> getStatistics(Long tenantId, Long schoolId) {
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Certificate::getTenantId, tenantId);
        List<Certificate> certificates = certificateMapper.selectList(wrapper);

        // 各类型证书数量
        Map<String, Long> typeCount = certificates.stream()
                .filter(c -> c.getCertType() != null)
                .collect(Collectors.groupingBy(Certificate::getCertType, Collectors.counting()));

        // 获奖等级分布
        Map<String, Long> awardLevelCount = certificates.stream()
                .filter(c -> c.getAwardLevel() != null)
                .collect(Collectors.groupingBy(Certificate::getAwardLevel, Collectors.counting()));

        // 审核状态分布
        Map<String, Long> reviewStatusCount = certificates.stream()
                .filter(c -> c.getReviewStatus() != null)
                .collect(Collectors.groupingBy(
                        c -> getReviewStatusText(c.getReviewStatus()),
                        Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", certificates.size());
        result.put("typeCount", typeCount);
        result.put("awardLevelCount", awardLevelCount);
        result.put("reviewStatusCount", reviewStatusCount);
        return result;
    }

    private String getReviewStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核通过";
            case 2: return "审核驳回";
            default: return "未知";
        }
    }
}
