package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.dto.ExamAnswerDTO;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Exam;
import com.example.learning.model.entity.ExamRecord;
import com.example.learning.service.ExamAnswerService;
import com.example.learning.service.ExamRecordService;
import com.example.learning.service.ExamService;
import com.example.learning.service.GradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 测评管理Controller
 */
@Tag(name = "测评管理")
@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final ExamRecordService examRecordService;
    private final ExamAnswerService examAnswerService;
    private final GradingService gradingService;

    /**
     * 分页查询测评列表
     *
     * @param dto 分页参数
     * @return 分页结果
     */
    @Operation(summary = "分页查询测评列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<Exam>> getPage(PageQueryDTO dto) {
        Page<Exam> page = examService.getPage(dto);
        PageResult<Exam> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return R.ok(result);
    }

    /**
     * 获取测评详情
     *
     * @param id 测评ID
     * @return 测评详情
     */
    @Operation(summary = "测评详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<Exam> getById(@PathVariable Long id) {
        Exam exam = examService.getById(id);
        return R.ok(exam);
    }

    /**
     * 创建测评
     *
     * @param exam 测评信息
     * @return 操作结果
     */
    @Operation(summary = "创建测评")
    @SaCheckLogin
    @PostMapping
    public R<Exam> create(@RequestBody Exam exam) {
        Exam created = examService.create(exam);
        return R.ok(created);
    }

    /**
     * 更新测评
     *
     * @param id   测评ID
     * @param exam 测评信息
     * @return 操作结果
     */
    @Operation(summary = "更新测评")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<Exam> update(@PathVariable Long id, @RequestBody Exam exam) {
        Exam updated = examService.update(id, exam);
        return R.ok(updated);
    }

    /**
     * 删除测评
     *
     * @param id 测评ID
     * @return 操作结果
     */
    @Operation(summary = "删除测评")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return R.ok();
    }

    /**
     * 发布测评
     *
     * @param id 测评ID
     * @return 操作结果
     */
    @Operation(summary = "发布测评")
    @SaCheckLogin
    @PostMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id) {
        examService.publish(id);
        return R.ok();
    }

    /**
     * 学生开始答题
     *
     * @param id       测评ID
     * @param studentId 学生ID（可选，默认当前登录用户）
     * @return 答题记录
     */
    @Operation(summary = "开始答题")
    @SaCheckLogin
    @PostMapping("/{id}/start")
    public R<ExamRecord> startExam(@PathVariable Long id,
                                   @RequestParam(required = false) Long studentId) {
        // 如果未传studentId，使用当前登录用户ID
        if (studentId == null) {
            studentId = StpUtil.getLoginIdAsLong();
        }
        ExamRecord record = examRecordService.startExam(id, studentId);
        return R.ok(record);
    }

    /**
     * 学生交卷
     *
     * @param id        测评ID
     * @param studentId 学生ID（可选，默认当前登录用户）
     * @return 操作结果
     */
    @Operation(summary = "交卷")
    @SaCheckLogin
    @PostMapping("/{id}/submit")
    public R<Void> submitExam(@PathVariable Long id,
                              @RequestParam(required = false) Long studentId) {
        if (studentId == null) {
            studentId = StpUtil.getLoginIdAsLong();
        }
        examRecordService.submitExam(id, studentId);
        return R.ok();
    }

    /**
     * 保存答案
     *
     * @param id  测评ID
     * @param dto 答案信息
     * @return 操作结果
     */
    @Operation(summary = "保存答案")
    @SaCheckLogin
    @PostMapping("/{id}/answers")
    public R<Void> saveAnswer(@PathVariable Long id, @RequestBody ExamAnswerDTO dto) {
        examAnswerService.saveAnswer(dto.getRecordId(), dto.getQuestionId(), dto.getStudentAnswer());
        return R.ok();
    }

    /**
     * 触发自动阅卷
     *
     * @param id 测评ID
     * @return 操作结果
     */
    @Operation(summary = "自动阅卷")
    @SaCheckLogin
    @PostMapping("/{id}/auto-grade")
    public R<Void> autoGrade(@PathVariable Long id) {
        gradingService.autoGradeExam(id);
        return R.ok();
    }
}
