package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.ReportTemplate;
import com.example.learning.service.ReportTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报告模板管理Controller
 */
@Tag(name = "报告模板管理")
@RestController
@RequestMapping("/api/v1/report-templates")
@RequiredArgsConstructor
public class ReportTemplateController {

    private final ReportTemplateService reportTemplateService;

    /**
     * 查询模板列表
     *
     * @param templateType 模板类型（可选）
     * @return 模板列表
     */
    @Operation(summary = "模板列表")
    @SaCheckLogin
    @GetMapping
    public R<List<ReportTemplate>> getList(@RequestParam(required = false) String templateType) {
        List<ReportTemplate> templates = reportTemplateService.getList(templateType);
        return R.ok(templates);
    }

    /**
     * 获取模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    @Operation(summary = "模板详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<ReportTemplate> getById(@PathVariable Long id) {
        ReportTemplate template = reportTemplateService.getById(id);
        return R.ok(template);
    }

    /**
     * 创建报告模板
     *
     * @param template 模板信息
     * @return 创建结果
     */
    @Operation(summary = "创建报告模板")
    @SaCheckLogin
    @PostMapping
    public R<ReportTemplate> create(@RequestBody ReportTemplate template) {
        ReportTemplate created = reportTemplateService.create(template);
        return R.ok(created);
    }

    /**
     * 更新报告模板
     *
     * @param id       模板ID
     * @param template 模板信息
     * @return 更新结果
     */
    @Operation(summary = "更新报告模板")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<ReportTemplate> update(@PathVariable Long id, @RequestBody ReportTemplate template) {
        ReportTemplate updated = reportTemplateService.update(id, template);
        return R.ok(updated);
    }

    /**
     * 删除报告模板
     *
     * @param id 模板ID
     * @return 操作结果
     */
    @Operation(summary = "删除报告模板")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        reportTemplateService.delete(id);
        return R.ok();
    }
}
