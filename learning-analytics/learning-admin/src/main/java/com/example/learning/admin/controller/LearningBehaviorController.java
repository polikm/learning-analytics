package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.LearningBehavior;
import com.example.learning.service.LearningBehaviorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 学习行为分析Controller
 */
@Tag(name = "学习行为分析")
@RestController
@RequestMapping("/api/v1/learning-behaviors")
@RequiredArgsConstructor
public class LearningBehaviorController {

    private final LearningBehaviorService learningBehaviorService;

    /**
     * 分页查询学习行为
     *
     * @param studentId    学生ID
     * @param behaviorType 行为类型
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param page         当前页码
     * @param pageSize     每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询学习行为")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<LearningBehavior>> getPage(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Page<LearningBehavior> pageResult = learningBehaviorService.getPage(
                studentId, behaviorType, startTime, endTime, page, pageSize);
        PageResult<LearningBehavior> result = PageResult.of(
                pageResult.getRecords(),
                pageResult.getTotal(),
                pageResult.getCurrent(),
                pageResult.getSize()
        );
        return R.ok(result);
    }

    /**
     * 学习行为统计
     *
     * @param studentId 学生ID
     * @return 统计结果
     */
    @Operation(summary = "学习行为统计")
    @SaCheckLogin
    @GetMapping("/statistics")
    public R<Map<String, Object>> getStatistics(@RequestParam(required = false) Long studentId) {
        // 如果未传studentId，使用当前登录用户ID
        if (studentId == null) {
            studentId = StpUtil.getLoginIdAsLong();
        }
        Map<String, Object> statistics = learningBehaviorService.getStatistics(studentId);
        return R.ok(statistics);
    }

    /**
     * 学习行为趋势
     *
     * @param studentId 学生ID
     * @param days      天数
     * @return 趋势数据
     */
    @Operation(summary = "学习行为趋势")
    @SaCheckLogin
    @GetMapping("/trend")
    public R<List<Map<String, Object>>> getTrend(
            @RequestParam(required = false) Long studentId,
            @RequestParam(defaultValue = "7") Integer days) {
        if (studentId == null) {
            studentId = StpUtil.getLoginIdAsLong();
        }
        List<Map<String, Object>> trend = learningBehaviorService.getTrend(studentId, days);
        return R.ok(trend);
    }

    /**
     * 记录学习行为
     *
     * @param behavior 学习行为实体
     * @return 操作结果
     */
    @Operation(summary = "记录学习行为")
    @SaCheckLogin
    @PostMapping
    public R<Void> recordBehavior(@RequestBody LearningBehavior behavior) {
        // 如果未设置studentId，使用当前登录用户ID
        if (behavior.getStudentId() == null) {
            behavior.setStudentId(StpUtil.getLoginIdAsLong());
        }
        learningBehaviorService.recordBehavior(behavior);
        return R.ok();
    }
}
