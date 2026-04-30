package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Certificate;
import com.example.learning.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 证书管理Controller
 */
@Tag(name = "证书管理")
@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    /**
     * 分页查询证书列表
     *
     * @param dto       分页参数
     * @param studentId 学生ID（可选）
     * @param certType  证书类型（可选）
     * @return 分页结果
     */
    @Operation(summary = "分页查询证书列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<Certificate>> getPage(PageQueryDTO dto,
                                              @RequestParam(required = false) Long studentId,
                                              @RequestParam(required = false) String certType) {
        Page<Certificate> page = certificateService.getPage(dto, studentId, certType);
        PageResult<Certificate> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取证书详情
     *
     * @param id 证书ID
     * @return 证书详情
     */
    @Operation(summary = "证书详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<Certificate> getById(@PathVariable Long id) {
        Certificate certificate = certificateService.getById(id);
        return R.ok(certificate);
    }

    /**
     * 创建证书
     *
     * @param certificate 证书信息
     * @return 创建结果
     */
    @Operation(summary = "创建证书")
    @SaCheckLogin
    @PostMapping
    public R<Certificate> create(@RequestBody Certificate certificate) {
        Certificate created = certificateService.create(certificate);
        return R.ok(created);
    }

    /**
     * 更新证书
     *
     * @param id          证书ID
     * @param certificate 证书信息
     * @return 更新结果
     */
    @Operation(summary = "更新证书")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Certificate> update(@PathVariable Long id, @RequestBody Certificate certificate) {
        Certificate updated = certificateService.update(id, certificate);
        return R.ok(updated);
    }

    /**
     * 删除证书
     *
     * @param id 证书ID
     * @return 操作结果
     */
    @Operation(summary = "删除证书")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        certificateService.delete(id);
        return R.ok();
    }

    /**
     * 审核证书
     *
     * @param id           证书ID
     * @param reviewStatus 审核状态
     * @param reviewRemark 审核备注
     * @return 操作结果
     */
    @Operation(summary = "审核证书")
    @SaCheckLogin
    @PostMapping("/{id}/review")
    public R<Void> review(@PathVariable Long id,
                          @RequestParam Integer reviewStatus,
                          @RequestParam(required = false) String reviewRemark) {
        certificateService.review(id, reviewStatus, reviewRemark);
        return R.ok();
    }

    /**
     * 获取证书统计信息
     *
     * @param tenantId 租户ID
     * @param schoolId 学校ID（可选）
     * @return 统计数据
     */
    @Operation(summary = "证书统计")
    @SaCheckLogin
    @GetMapping("/statistics")
    public R<Map<String, Object>> getStatistics(@RequestParam Long tenantId,
                                                 @RequestParam(required = false) Long schoolId) {
        Map<String, Object> statistics = certificateService.getStatistics(tenantId, schoolId);
        return R.ok(statistics);
    }
}
