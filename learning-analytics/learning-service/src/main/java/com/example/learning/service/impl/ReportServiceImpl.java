package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Report;
import com.example.learning.model.entity.ReportTemplate;
import com.example.learning.model.mapper.ReportMapper;
import com.example.learning.model.mapper.ReportTemplateMapper;
import com.example.learning.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报告服务实现类
 */
@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ReportTemplateMapper reportTemplateMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<Report> getPage(PageQueryDTO dto) {
        Page<Report> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Report::getCreatedAt);
        return reportMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generate(Long templateId, Long targetType, Long targetId, List<Long> scopeIds) {
        ReportTemplate template = reportTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        Report report = new Report();
        report.setTenantId(template.getTenantId());
        report.setReportName(template.getTemplateName() + " - " + LocalDateTime.now().toString().substring(0, 10));
        report.setReportType(template.getTemplateType());
        report.setTemplateId(templateId);
        report.setTargetType(String.valueOf(targetType));
        report.setTargetId(String.valueOf(targetId));
        try {
            report.setScopeIds(objectMapper.writeValueAsString(scopeIds));
        } catch (Exception e) {
            report.setScopeIds("[]");
        }
        report.setGenerateStatus(0); // 生成中
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportMapper.insert(report);

        // 异步执行报告生成
        asyncGenerateReport(report.getId(), template);

        return report.getId();
    }

    @Async
    public void asyncGenerateReport(Long reportId, ReportTemplate template) {
        try {
            Report update = new Report();
            update.setId(reportId);

            // 模拟报告生成过程
            // TODO: 实际项目中这里应调用报告引擎生成PDF
            Thread.sleep(2000);

            update.setGenerateStatus(1); // 生成完成
            update.setFileUrl("/reports/" + reportId + ".pdf");
            update.setFileSize(1024L);
            update.setGeneratedAt(LocalDateTime.now());
            update.setUpdatedAt(LocalDateTime.now());
            reportMapper.updateById(update);
        } catch (Exception e) {
            log.error("报告生成失败, reportId={}", reportId, e);
            Report update = new Report();
            update.setId(reportId);
            update.setGenerateStatus(2); // 生成失败
            update.setErrorMsg(e.getMessage());
            update.setUpdatedAt(LocalDateTime.now());
            reportMapper.updateById(update);
        }
    }

    @Override
    public Report getGenerateStatus(Long reportId) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return report;
    }

    @Override
    public String download(Long reportId) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (report.getGenerateStatus() != 1) {
            throw new BusinessException("报告尚未生成完成");
        }
        return report.getFileUrl();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchGenerate(Long templateId, Long targetType, List<Long> targetIds) {
        List<Long> reportIds = new ArrayList<>();
        for (Long targetId : targetIds) {
            Long reportId = generate(templateId, targetType, targetId, null);
            reportIds.add(reportId);
        }
        return reportIds;
    }

    @Override
    public List<Report> getStudentReports(Long studentId) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getTargetType, "1") // 学生类型
                .eq(Report::getTargetId, String.valueOf(studentId))
                .orderByDesc(Report::getCreatedAt);
        return reportMapper.selectList(wrapper);
    }
}
