package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.ParentStudent;
import com.example.learning.service.ParentStudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家长学生关联管理Controller
 */
@Tag(name = "家长学生关联管理")
@RestController
@RequestMapping("/api/v1/parent-students")
@RequiredArgsConstructor
public class ParentStudentController {

    private final ParentStudentService parentStudentService;

    /**
     * 查询关联列表
     *
     * @param parentId  家长ID
     * @param studentId 学生ID
     * @return 关联列表
     */
    @Operation(summary = "查询关联列表")
    @SaCheckLogin
    @GetMapping
    public R<List<ParentStudent>> getList(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Long studentId) {
        List<ParentStudent> list = parentStudentService.getList(parentId, studentId);
        return R.ok(list);
    }

    /**
     * 新增关联
     *
     * @param ps 家长学生关联实体
     * @return 操作结果
     */
    @Operation(summary = "新增关联")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@Valid @RequestBody ParentStudent ps) {
        parentStudentService.create(ps);
        return R.ok();
    }

    /**
     * 更新关联
     *
     * @param id 关联ID
     * @param ps 家长学生关联实体
     * @return 操作结果
     */
    @Operation(summary = "更新关联")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody ParentStudent ps) {
        parentStudentService.update(id, ps);
        return R.ok();
    }

    /**
     * 删除关联
     *
     * @param id 关联ID
     * @return 操作结果
     */
    @Operation(summary = "删除关联")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        parentStudentService.delete(id);
        return R.ok();
    }

    /**
     * 获取家长关联的学生列表
     *
     * @param parentId 家长ID
     * @return 关联列表
     */
    @Operation(summary = "获取家长关联的学生列表")
    @SaCheckLogin
    @GetMapping("/parent/{parentId}")
    public R<List<ParentStudent>> getByParent(@PathVariable Long parentId) {
        List<ParentStudent> list = parentStudentService.getByParent(parentId);
        return R.ok(list);
    }

    /**
     * 获取学生关联的家长列表
     *
     * @param studentId 学生ID
     * @return 关联列表
     */
    @Operation(summary = "获取学生关联的家长列表")
    @SaCheckLogin
    @GetMapping("/student/{studentId}")
    public R<List<ParentStudent>> getByStudent(@PathVariable Long studentId) {
        List<ParentStudent> list = parentStudentService.getByStudent(studentId);
        return R.ok(list);
    }
}
