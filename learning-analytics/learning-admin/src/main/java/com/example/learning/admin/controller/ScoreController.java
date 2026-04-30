package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.vo.ClassRankingVO;
import com.example.learning.model.vo.ExamStatisticsVO;
import com.example.learning.model.vo.QuestionAnalysisVO;
import com.example.learning.model.vo.StudentTrendVO;
import com.example.learning.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 成绩分析Controller
 */
@Tag(name = "成绩分析")
@RestController
@RequestMapping("/api/v1/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * 测评统计分析
     *
     * @param examId 测评ID
     * @return 统计结果
     */
    @Operation(summary = "测评统计分析")
    @SaCheckLogin
    @GetMapping("/exam/{examId}/statistics")
    public R<ExamStatisticsVO> getStatistics(@PathVariable Long examId) {
        ExamStatisticsVO statistics = scoreService.getStatistics(examId);
        return R.ok(statistics);
    }

    /**
     * 题目分析
     *
     * @param examId 测评ID
     * @return 题目分析列表
     */
    @Operation(summary = "题目分析")
    @SaCheckLogin
    @GetMapping("/exam/{examId}/question-analysis")
    public R<List<QuestionAnalysisVO>> getQuestionAnalysis(@PathVariable Long examId) {
        List<QuestionAnalysisVO> analysis = scoreService.getQuestionAnalysis(examId);
        return R.ok(analysis);
    }

    /**
     * 学生成绩趋势
     *
     * @param studentId 学生ID
     * @param subjectId 学科ID（可选）
     * @return 成绩趋势列表
     */
    @Operation(summary = "学生成绩趋势")
    @SaCheckLogin
    @GetMapping("/student/{studentId}/trend")
    public R<List<StudentTrendVO>> getStudentTrend(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long subjectId) {
        List<StudentTrendVO> trend = scoreService.getStudentTrend(studentId, subjectId);
        return R.ok(trend);
    }

    /**
     * 班级排名
     *
     * @param classId 班级ID
     * @param examId  测评ID
     * @return 排名列表
     */
    @Operation(summary = "班级排名")
    @SaCheckLogin
    @GetMapping("/class/{classId}/ranking")
    public R<List<ClassRankingVO>> getClassRanking(
            @PathVariable Long classId,
            @RequestParam Long examId) {
        List<ClassRankingVO> ranking = scoreService.getClassRanking(classId, examId);
        return R.ok(ranking);
    }
}
