package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.dto.SchoolDTO;
import com.example.learning.model.entity.School;
import com.example.learning.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学校管理Controller
 */
@Tag(name = "学校管理")
@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    /**
     * 分页查询学校列表
     *
     * @param dto 分页查询DTO
     * @return 分页结果
     */
    @Operation(summary = "分页查询学校列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<School>> getPage(PageQueryDTO dto) {
        Page<School> page = schoolService.getPage(dto);
        PageResult<School> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取学校详情
     *
     * @param id 学校ID
     * @return 学校实体
     */
    @Operation(summary = "学校详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<School> getById(@PathVariable Long id) {
        School school = schoolService.getById(id);
        return R.ok(school);
    }

    /**
     * 创建学校
     *
     * @param dto 学校信息DTO
     * @return 操作结果
     */
    @Operation(summary = "创建学校")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@Valid @RequestBody SchoolDTO dto) {
        schoolService.create(dto);
        return R.ok();
    }

    /**
     * 更新学校
     *
     * @param id  学校ID
     * @param dto 学校信息DTO
     * @return 操作结果
     */
    @Operation(summary = "更新学校")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody SchoolDTO dto) {
        schoolService.update(id, dto);
        return R.ok();
    }

    /**
     * 删除学校
     *
     * @param id 学校ID
     * @return 操作结果
     */
    @Operation(summary = "删除学校")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        schoolService.delete(id);
        return R.ok();
    }
}
