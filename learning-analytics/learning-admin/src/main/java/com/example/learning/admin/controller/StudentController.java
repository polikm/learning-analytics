package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.StudentProfile;
import com.example.learning.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学生管理Controller
 */
@Tag(name = "学生管理")
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 分页查询学生列表
     *
     * @param dto 分页查询DTO
     * @return 分页结果
     */
    @Operation(summary = "分页查询学生列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<StudentProfile>> getPage(PageQueryDTO dto) {
        Page<StudentProfile> page = studentService.getPage(dto);
        PageResult<StudentProfile> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取学生详情
     *
     * @param id 学生ID
     * @return 学生档案实体
     */
    @Operation(summary = "学生详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<StudentProfile> getById(@PathVariable Long id) {
        StudentProfile student = studentService.getById(id);
        return R.ok(student);
    }

    /**
     * 创建学生
     *
     * @param studentProfile 学生档案实体
     * @return 操作结果
     */
    @Operation(summary = "创建学生")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@Valid @RequestBody StudentProfile studentProfile) {
        studentService.create(studentProfile);
        return R.ok();
    }

    /**
     * 更新学生
     *
     * @param id             学生ID
     * @param studentProfile 学生档案实体
     * @return 操作结果
     */
    @Operation(summary = "更新学生")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody StudentProfile studentProfile) {
        studentService.update(id, studentProfile);
        return R.ok();
    }

    /**
     * 删除学生
     *
     * @param id 学生ID
     * @return 操作结果
     */
    @Operation(summary = "删除学生")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return R.ok();
    }
}
