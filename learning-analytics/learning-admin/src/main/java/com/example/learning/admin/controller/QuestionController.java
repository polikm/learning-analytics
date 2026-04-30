package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Question;
import com.example.learning.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 题目管理Controller
 */
@Tag(name = "题目管理")
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 分页查询题目列表（多条件筛选）
     *
     * @param dto          分页参数
     * @param subjectId    学科ID
     * @param questionType 题型
     * @param difficulty   难度
     * @param knowledgeId  知识点ID
     * @param reviewStatus 审核状态
     * @param keyword      关键词
     * @return 分页结果
     */
    @Operation(summary = "分页查询题目列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<Question>> getPage(
            PageQueryDTO dto,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer questionType,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) Long knowledgeId,
            @RequestParam(required = false) Integer reviewStatus,
            @RequestParam(required = false) String keyword) {
        Page<Question> page = questionService.getPage(dto, subjectId, questionType,
                difficulty, knowledgeId, reviewStatus, keyword);
        PageResult<Question> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取题目详情
     *
     * @param id 题目ID
     * @return 题目详情
     */
    @Operation(summary = "题目详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<Question> getById(@PathVariable Long id) {
        Question question = questionService.getById(id);
        return R.ok(question);
    }

    /**
     * 创建题目
     *
     * @param question 题目信息
     * @return 操作结果
     */
    @Operation(summary = "创建题目")
    @SaCheckLogin
    @PostMapping
    public R<Question> create(@RequestBody Question question) {
        Question created = questionService.create(question);
        return R.ok(created);
    }

    /**
     * 更新题目
     *
     * @param id       题目ID
     * @param question 题目信息
     * @return 操作结果
     */
    @Operation(summary = "更新题目")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Question> update(@PathVariable Long id, @RequestBody Question question) {
        Question updated = questionService.update(id, question);
        return R.ok(updated);
    }

    /**
     * 删除题目
     *
     * @param id 题目ID
     * @return 操作结果
     */
    @Operation(summary = "删除题目")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        questionService.delete(id);
        return R.ok();
    }

    /**
     * 审核题目
     *
     * @param id           题目ID
     * @param reviewStatus 审核状态
     * @return 操作结果
     */
    @Operation(summary = "审核题目")
    @SaCheckLogin
    @PostMapping("/{id}/review")
    public R<Void> review(@PathVariable Long id, @RequestParam Integer reviewStatus) {
        questionService.review(id, reviewStatus);
        return R.ok();
    }
}
