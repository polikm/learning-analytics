package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.KnowledgePoint;
import com.example.learning.service.KnowledgePointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识点管理Controller
 */
@Tag(name = "知识点管理")
@RestController
@RequestMapping("/api/v1/knowledge-points")
@RequiredArgsConstructor
public class KnowledgePointController {

    private final KnowledgePointService knowledgePointService;

    /**
     * 查询知识点树形列表
     *
     * @param subjectId 学科ID
     * @return 知识点树形列表
     */
    @Operation(summary = "查询知识点树形列表")
    @SaCheckLogin
    @GetMapping
    public R<List<KnowledgePoint>> getTree(@RequestParam Long subjectId) {
        List<KnowledgePoint> tree = knowledgePointService.getTree(subjectId);
        return R.ok(tree);
    }

    /**
     * 创建知识点
     *
     * @param knowledgePoint 知识点信息
     * @return 操作结果
     */
    @Operation(summary = "创建知识点")
    @SaCheckLogin
    @PostMapping
    public R<KnowledgePoint> create(@RequestBody KnowledgePoint knowledgePoint) {
        KnowledgePoint created = knowledgePointService.create(knowledgePoint);
        return R.ok(created);
    }

    /**
     * 更新知识点
     *
     * @param id             知识点ID
     * @param knowledgePoint 知识点信息
     * @return 操作结果
     */
    @Operation(summary = "更新知识点")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<KnowledgePoint> update(@PathVariable Long id, @RequestBody KnowledgePoint knowledgePoint) {
        KnowledgePoint updated = knowledgePointService.update(id, knowledgePoint);
        return R.ok(updated);
    }

    /**
     * 删除知识点
     *
     * @param id 知识点ID
     * @return 操作结果
     */
    @Operation(summary = "删除知识点")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        knowledgePointService.delete(id);
        return R.ok();
    }
}
