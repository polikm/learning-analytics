package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.Subject;
import com.example.learning.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学科管理Controller
 */
@Tag(name = "学科管理")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * 查询学科列表
     *
     * @param tenantId 租户ID
     * @return 学科列表
     */
    @Operation(summary = "查询学科列表")
    @SaCheckLogin
    @GetMapping
    public R<List<Subject>> getList(@RequestParam(required = false) Long tenantId) {
        List<Subject> list = subjectService.getList(tenantId);
        return R.ok(list);
    }

    /**
     * 创建学科
     *
     * @param subject 学科信息
     * @return 操作结果
     */
    @Operation(summary = "创建学科")
    @SaCheckLogin
    @PostMapping
    public R<Subject> create(@RequestBody Subject subject) {
        Subject created = subjectService.create(subject);
        return R.ok(created);
    }

    /**
     * 更新学科
     *
     * @param id      学科ID
     * @param subject 学科信息
     * @return 操作结果
     */
    @Operation(summary = "更新学科")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Subject> update(@PathVariable Long id, @RequestBody Subject subject) {
        Subject updated = subjectService.update(id, subject);
        return R.ok(updated);
    }

    /**
     * 删除学科
     *
     * @param id 学科ID
     * @return 操作结果
     */
    @Operation(summary = "删除学科")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return R.ok();
    }
}
