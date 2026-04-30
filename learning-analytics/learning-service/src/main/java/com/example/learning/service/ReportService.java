package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Report;

import java.util.List;

/**
 * 报告服务接口
 */
public interface ReportService {

    /**
     * 分页查询报告列表
     *
     * @param dto 分页参数
     * @return 分页结果
     */
    Page<Report> getPage(PageQueryDTO dto);

    /**
     * 生成报告（异步）
     *
     * @param templateId 模板ID
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @param scopeIds   范围ID列表
     * @return 报告ID
     */
    Long generate(Long templateId, Long targetType, Long targetId, List<Long> scopeIds);

    /**
     * 查询报告生成状态
     *
     * @param reportId 报告ID
     * @return 生成状态信息
     */
    Report getGenerateStatus(Long reportId);

    /**
     * 下载报告PDF
     *
     * @param reportId 报告ID
     * @return 文件URL
     */
    String download(Long reportId);

    /**
     * 批量生成报告
     *
     * @param templateId 模板ID
     * @param targetType 目标类型
     * @param targetIds  目标ID列表
     * @return 生成的报告ID列表
     */
    List<Long> batchGenerate(Long templateId, Long targetType, List<Long> targetIds);

    /**
     * 获取学生报告列表
     *
     * @param studentId 学生ID
     * @return 报告列表
     */
    List<Report> getStudentReports(Long studentId);
}
