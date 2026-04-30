package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.Grade;
import com.example.learning.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 年级管理Controller
 */
@Tag(name = "年级管理")
@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    /**
     * 按学校查询年级列表
     *
     * @param schoolId 学校ID
     * @return 年级列表
     */
    @Operation(summary = "按学校查询年级列表")
    @SaCheckLogin
    @GetMapping
    public R<List<Grade>> getList(@RequestParam Long schoolId) {
        List<Grade> list = gradeService.getList(schoolId);
        return R.ok(list);
    }

    /**
     * 创建年级
     *
     * @param grade 年级实体
     * @return 操作结果
     */
    @Operation(summary = "创建年级")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@Valid @RequestBody Grade grade) {
        gradeService.create(grade);
        return R.ok();
    }

    /**
     * 更新年级
     *
     * @param id    年级ID
     * @param grade 年级实体
     * @return 操作结果
     */
    @Operation(summary = "更新年级")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody Grade grade) {
        gradeService.update(id, grade);
        return R.ok();
    }

    /**
     * 删除年级
     *
     * @param id 年级ID
     * @return 操作结果
     */
    @Operation(summary = "删除年级")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        gradeService.delete(id);
        return R.ok();
    }
}
