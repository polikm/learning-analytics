package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Report;
import com.example.learning.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报告管理Controller
 */
@Tag(name = "报告管理")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 分页查询报告列表
     *
     * @param dto 分页参数
     * @return 分页结果
     */
    @Operation(summary = "报告列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<Report>> getPage(PageQueryDTO dto) {
        Page<Report> page = reportService.getPage(dto);
        PageResult<Report> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return R.ok(result);
    }

    /**
     * 生成报告
     *
     * @param templateId 模板ID
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @param scopeIds   范围ID列表（可选）
     * @return 报告ID
     */
    @Operation(summary = "生成报告")
    @SaCheckLogin
    @PostMapping("/generate")
    public R<Long> generate(@RequestParam Long templateId,
                            @RequestParam Long targetType,
                            @RequestParam Long targetId,
                            @RequestParam(required = false) List<Long> scopeIds) {
        Long reportId = reportService.generate(templateId, targetType, targetId, scopeIds);
        return R.ok(reportId);
    }

    /**
     * 查询报告生成状态
     *
     * @param id 报告ID
     * @return 生成状态
     */
    @Operation(summary = "报告生成状态")
    @SaCheckLogin
    @GetMapping("/{id}/status")
    public R<Report> getGenerateStatus(@PathVariable Long id) {
        Report report = reportService.getGenerateStatus(id);
        return R.ok(report);
    }

    /**
     * 下载报告
     *
     * @param id 报告ID
     * @return 文件URL
     */
    @Operation(summary = "下载报告")
    @SaCheckLogin
    @GetMapping("/{id}/download")
    public R<String> download(@PathVariable Long id) {
        String fileUrl = reportService.download(id);
        return R.ok(fileUrl);
    }

    /**
     * 批量生成报告
     *
     * @param templateId 模板ID
     * @param targetType 目标类型
     * @param targetIds  目标ID列表
     * @return 生成的报告ID列表
     */
    @Operation(summary = "批量生成报告")
    @SaCheckLogin
    @PostMapping("/batch-generate")
    public R<List<Long>> batchGenerate(@RequestParam Long templateId,
                                       @RequestParam Long targetType,
                                       @RequestBody List<Long> targetIds) {
        List<Long> reportIds = reportService.batchGenerate(templateId, targetType, targetIds);
        return R.ok(reportIds);
    }

    /**
     * 获取学生报告列表
     *
     * @param studentId 学生ID
     * @return 报告列表
     */
    @Operation(summary = "学生报告列表")
    @SaCheckLogin
    @GetMapping("/student/{studentId}")
    public R<List<Report>> getStudentReports(@PathVariable Long studentId) {
        List<Report> reports = reportService.getStudentReports(studentId);
        return R.ok(reports);
    }
}
