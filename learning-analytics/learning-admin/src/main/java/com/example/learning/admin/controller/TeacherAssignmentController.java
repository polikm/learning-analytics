package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.TeacherAssignment;
import com.example.learning.service.TeacherAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师任课分配Controller
 */
@Tag(name = "教师任课分配")
@RestController
@RequestMapping("/api/v1/teacher-assignments")
@RequiredArgsConstructor
public class TeacherAssignmentController {

    private final TeacherAssignmentService teacherAssignmentService;

    /**
     * 查询任课分配列表
     *
     * @param teacherId   教师ID
     * @param schoolId    学校ID
     * @param subjectId   学科ID
     * @param academicYear 学年
     * @return 任课分配列表
     */
    @Operation(summary = "查询任课分配列表")
    @SaCheckLogin
    @GetMapping
    public R<List<TeacherAssignment>> getList(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String academicYear) {
        List<TeacherAssignment> list = teacherAssignmentService.getList(teacherId, schoolId, subjectId, academicYear);
        return R.ok(list);
    }

    /**
     * 新增任课分配
     *
     * @param assignment 任课分配实体
     * @return 操作结果
     */
    @Operation(summary = "新增任课分配")
    @SaCheckLogin
    @PostMapping
    public R<Void> create(@Valid @RequestBody TeacherAssignment assignment) {
        teacherAssignmentService.create(assignment);
        return R.ok();
    }

    /**
     * 更新任课分配
     *
     * @param id         任课分配ID
     * @param assignment 任课分配实体
     * @return 操作结果
     */
    @Operation(summary = "更新任课分配")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody TeacherAssignment assignment) {
        teacherAssignmentService.update(id, assignment);
        return R.ok();
    }

    /**
     * 删除任课分配
     *
     * @param id 任课分配ID
     * @return 操作结果
     */
    @Operation(summary = "删除任课分配")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        teacherAssignmentService.delete(id);
        return R.ok();
    }

    /**
     * 获取教师的任课信息
     *
     * @param teacherId 教师ID
     * @return 任课分配列表
     */
    @Operation(summary = "获取教师的任课信息")
    @SaCheckLogin
    @GetMapping("/teacher/{teacherId}")
    public R<List<TeacherAssignment>> getByTeacher(@PathVariable Long teacherId) {
        List<TeacherAssignment> list = teacherAssignmentService.getByTeacher(teacherId);
        return R.ok(list);
    }
}
