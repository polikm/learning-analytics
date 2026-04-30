package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.ReportTemplate;
import com.example.learning.model.mapper.ReportTemplateMapper;
import com.example.learning.service.ReportTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报告模板服务实现类
 */
@Service
public class ReportTemplateServiceImpl implements ReportTemplateService {

    @Autowired
    private ReportTemplateMapper reportTemplateMapper;

    @Override
    public List<ReportTemplate> getList(String templateType) {
        LambdaQueryWrapper<ReportTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(templateType)) {
            wrapper.eq(ReportTemplate::getTemplateType, templateType);
        }
        wrapper.eq(ReportTemplate::getStatus, 1)
                .orderByDesc(ReportTemplate::getIsDefault)
                .orderByDesc(ReportTemplate::getCreatedAt);
        return reportTemplateMapper.selectList(wrapper);
    }

    @Override
    public ReportTemplate getById(Long id) {
        ReportTemplate template = reportTemplateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportTemplate create(ReportTemplate template) {
        template.setStatus(1); // 启用
        template.setIsDefault(0); // 非默认
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        reportTemplateMapper.insert(template);
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportTemplate update(Long id, ReportTemplate template) {
        ReportTemplate existing = reportTemplateMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        template.setId(id);
        template.setUpdatedAt(LocalDateTime.now());
        reportTemplateMapper.updateById(template);
        return reportTemplateMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ReportTemplate existing = reportTemplateMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        reportTemplateMapper.deleteById(id);
    }
}
