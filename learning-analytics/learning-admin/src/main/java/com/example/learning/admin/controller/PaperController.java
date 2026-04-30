package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.dto.PaperDTO;
import com.example.learning.model.entity.Paper;
import com.example.learning.model.entity.PaperQuestion;
import com.example.learning.model.vo.PaperDetailVO;
import com.example.learning.service.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 试卷管理Controller
 */
@Tag(name = "试卷管理")
@RestController
@RequestMapping("/api/v1/papers")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;

    /**
     * 分页查询试卷列表
     *
     * @param dto 分页参数
     * @return 分页结果
     */
    @Operation(summary = "分页查询试卷列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<Paper>> getPage(PageQueryDTO dto) {
        Page<Paper> page = paperService.getPage(dto);
        PageResult<Paper> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取试卷详情（含题目列表）
     *
     * @param id 试卷ID
     * @return 试卷详情
     */
    @Operation(summary = "试卷详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PaperDetailVO> getById(@PathVariable Long id) {
        PaperDetailVO detail = paperService.getById(id);
        return R.ok(detail);
    }

    /**
     * 创建试卷（含题目关联）
     * 请求体格式: { "paper": {...}, "questions": [...] }
     *
     * @param paperDTO 试卷DTO
     * @return 创建的试卷
     */
    @Operation(summary = "创建试卷")
    @SaCheckLogin
    @PostMapping
    public R<Paper> create(@RequestBody PaperDTO paperDTO) {
        Paper created = paperService.create(paperDTO.getPaper(), paperDTO.getQuestions());
        return R.ok(created);
    }

    /**
     * 更新试卷（含题目关联）
     * 请求体格式: { "paper": {...}, "questions": [...] }
     *
     * @param id       试卷ID
     * @param paperDTO 试卷DTO
     * @return 更新后的试卷
     */
    @Operation(summary = "更新试卷")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Paper> update(@PathVariable Long id, @RequestBody PaperDTO paperDTO) {
        Paper updated = paperService.update(id, paperDTO.getPaper(), paperDTO.getQuestions());
        return R.ok(updated);
    }

    /**
     * 删除试卷
     *
     * @param id 试卷ID
     * @return 操作结果
     */
    @Operation(summary = "删除试卷")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        paperService.delete(id);
        return R.ok();
    }

    /**
     * 智能组卷
     * 请求体格式: { "subjectId": 1, "knowledgeIds": "1,2,3", "difficulty": 3, "typeRatio": {"1": 10, "2": 5}, "totalCount": 15 }
     *
     * @param requestBody 智能组卷参数
     * @return 生成的试卷
     */
    @Operation(summary = "智能组卷")
    @SaCheckLogin
    @PostMapping("/auto-generate")
    public R<Paper> autoGenerate(@RequestBody Map<String, Object> requestBody) {
        Long subjectId = Long.valueOf(requestBody.get("subjectId").toString());
        String knowledgeIds = requestBody.get("knowledgeIds") != null
                ? requestBody.get("knowledgeIds").toString() : null;
        Integer difficulty = requestBody.get("difficulty") != null
                ? Integer.valueOf(requestBody.get("difficulty").toString()) : null;
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> typeRatio = (Map<Integer, Integer>) requestBody.get("typeRatio");
        Integer totalCount = Integer.valueOf(requestBody.get("totalCount").toString());

        Paper paper = paperService.autoGenerate(subjectId, knowledgeIds, difficulty, typeRatio, totalCount);
        return R.ok(paper);
    }
}
