package com.example.learning.service;

import com.example.learning.model.entity.ReportTemplate;

import java.util.List;

/**
 * 报告模板服务接口
 */
public interface ReportTemplateService {

    /**
     * 按类型查询模板列表
     *
     * @param templateType 模板类型（可选）
     * @return 模板列表
     */
    List<ReportTemplate> getList(String templateType);

    /**
     * 获取模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    ReportTemplate getById(Long id);

    /**
     * 创建报告模板
     *
     * @param template 模板信息
     * @return 创建后的模板
     */
    ReportTemplate create(ReportTemplate template);

    /**
     * 更新报告模板
     *
     * @param id       模板ID
     * @param template 模板信息
     * @return 更新后的模板
     */
    ReportTemplate update(Long id, ReportTemplate template);

    /**
     * 删除报告模板
     *
     * @param id 模板ID
     */
    void delete(Long id);
}
