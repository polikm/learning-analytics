package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Tenant;
import com.example.learning.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 租户管理Controller
 */
@Tag(name = "租户管理")
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * 分页查询租户列表
     *
     * @param dto 分页查询DTO
     * @return 分页结果
     */
    @Operation(summary = "分页查询租户列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<Tenant>> getPage(PageQueryDTO dto) {
        Page<Tenant> page = tenantService.getPage(dto);
        PageResult<Tenant> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取租户详情
     *
     * @param id 租户ID
     * @return 租户实体
     */
    @Operation(summary = "租户详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<Tenant> getById(@PathVariable Long id) {
        Tenant tenant = tenantService.getById(id);
        return R.ok(tenant);
    }

    /**
     * 创建租户
     *
     * @param tenant 租户实体
     * @return 操作结果
     */
    @Operation(summary = "创建租户")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@Valid @RequestBody Tenant tenant) {
        tenantService.create(tenant);
        return R.ok();
    }

    /**
     * 更新租户
     *
     * @param id     租户ID
     * @param tenant 租户实体
     * @return 操作结果
     */
    @Operation(summary = "更新租户")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody Tenant tenant) {
        tenantService.update(id, tenant);
        return R.ok();
    }

    /**
     * 删除租户
     *
     * @param id 租户ID
     * @return 操作结果
     */
    @Operation(summary = "删除租户")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return R.ok();
    }
}
