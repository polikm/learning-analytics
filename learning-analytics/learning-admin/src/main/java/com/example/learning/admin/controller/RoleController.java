package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.SysRole;
import com.example.learning.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理Controller
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     *
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询角色列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<SysRole>> getPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        PageQueryDTO dto = new PageQueryDTO(page, size);
        Page<SysRole> pageResult = roleService.getPage(dto);
        PageResult<SysRole> result = PageResult.of(
                pageResult.getRecords(),
                pageResult.getTotal(),
                pageResult.getCurrent(),
                pageResult.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取全部角色（下拉用）
     *
     * @return 角色列表
     */
    @Operation(summary = "获取全部角色")
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<SysRole>> getAllRoles() {
        return R.ok(roleService.getAllRoles());
    }

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @Operation(summary = "角色详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<SysRole> getById(@PathVariable Long id) {
        return R.ok(roleService.getById(id));
    }

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 操作结果
     */
    @Operation(summary = "创建角色")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@RequestBody SysRole role) {
        roleService.create(role);
        return R.ok();
    }

    /**
     * 更新角色
     *
     * @param id   角色ID
     * @param role 角色信息
     * @return 操作结果
     */
    @Operation(summary = "更新角色")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody SysRole role) {
        roleService.update(id, role);
        return R.ok();
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @Operation(summary = "删除角色")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return R.ok();
    }

    /**
     * 获取角色权限ID列表
     *
     * @param id 角色ID
     * @return 权限ID列表
     */
    @Operation(summary = "获取角色权限")
    @SaCheckLogin
    @GetMapping("/{id}/permissions")
    public R<List<Long>> getRolePermissions(@PathVariable Long id) {
        return R.ok(roleService.getRolePermissionIds(id));
    }

    /**
     * 分配权限
     *
     * @param id            角色ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    @Operation(summary = "分配权限")
    @SaCheckLogin
    @PutMapping("/{id}/permissions")
    public R<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return R.ok();
    }
}
