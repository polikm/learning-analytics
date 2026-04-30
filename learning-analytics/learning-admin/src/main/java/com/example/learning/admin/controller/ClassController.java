package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.Class;
import com.example.learning.model.entity.StudentProfile;
import com.example.learning.service.ClassService;
import com.example.learning.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理Controller
 */
@Tag(name = "班级管理")
@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    private final StudentService studentService;

    /**
     * 按年级查询班级列表
     *
     * @param gradeId 年级ID
     * @return 班级列表
     */
    @Operation(summary = "按年级查询班级列表")
    @SaCheckLogin
    @GetMapping
    public R<List<Class>> getList(@RequestParam Long gradeId) {
        List<Class> list = classService.getList(gradeId);
        return R.ok(list);
    }

    /**
     * 创建班级
     *
     * @param clazz 班级实体
     * @return 操作结果
     */
    @Operation(summary = "创建班级")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@Valid @RequestBody Class clazz) {
        classService.create(clazz);
        return R.ok();
    }

    /**
     * 更新班级
     *
     * @param id    班级ID
     * @param clazz 班级实体
     * @return 操作结果
     */
    @Operation(summary = "更新班级")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody Class clazz) {
        classService.update(id, clazz);
        return R.ok();
    }

    /**
     * 删除班级
     *
     * @param id 班级ID
     * @return 操作结果
     */
    @Operation(summary = "删除班级")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        classService.delete(id);
        return R.ok();
    }

    /**
     * 查询班级学生列表
     *
     * @param classId 班级ID
     * @return 学生列表
     */
    @Operation(summary = "查询班级学生列表")
    @SaCheckLogin
    @GetMapping("/{classId}/students")
    public R<List<StudentProfile>> getStudentsByClassId(@PathVariable Long classId) {
        List<StudentProfile> list = studentService.getByClassId(classId);
        return R.ok(list);
    }
}
