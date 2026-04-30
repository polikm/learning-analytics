package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.StudentProfileSnapshot;
import com.example.learning.service.KnowledgeMasteryService;
import com.example.learning.service.ProfileService;
import com.example.learning.service.SnapshotService;
import com.example.learning.service.WarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学情档案Controller
 */
@Tag(name = "学情档案")
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final KnowledgeMasteryService knowledgeMasteryService;
    private final WarningService warningService;
    private final SnapshotService snapshotService;

    /**
     * 获取学生学情档案
     *
     * @param studentId 学生ID
     * @return 学情档案数据
     */
    @Operation(summary = "学生学情档案")
    @SaCheckLogin
    @GetMapping("/student/{studentId}")
    public R<Map<String, Object>> getStudentProfile(@PathVariable Long studentId) {
        Map<String, Object> profile = profileService.getStudentProfile(studentId);
        return R.ok(profile);
    }

    /**
     * 班级学情概览
     *
     * @param classId 班级ID
     * @return 班级概览数据
     */
    @Operation(summary = "班级学情概览")
    @SaCheckLogin
    @GetMapping("/class/{classId}/overview")
    public R<Map<String, Object>> getClassOverview(@PathVariable Long classId) {
        Map<String, Object> overview = profileService.getClassOverview(classId);
        return R.ok(overview);
    }

    /**
     * 年级学情概览
     *
     * @param gradeId 年级ID
     * @return 年级概览数据
     */
    @Operation(summary = "年级学情概览")
    @SaCheckLogin
    @GetMapping("/grade/{gradeId}/overview")
    public R<Map<String, Object>> getGradeOverview(@PathVariable Long gradeId) {
        Map<String, Object> overview = profileService.getGradeOverview(gradeId);
        return R.ok(overview);
    }

    /**
     * 知识掌握画像
     *
     * @param studentId 学生ID
     * @param subjectId 学科ID（可选）
     * @return 知识掌握度数据
     */
    @Operation(summary = "知识掌握画像")
    @SaCheckLogin
    @GetMapping("/student/{studentId}/knowledge-mastery")
    public R<Map<String, Object>> getKnowledgeMastery(@PathVariable Long studentId,
                                                       @RequestParam(required = false) Long subjectId) {
        Map<String, Object> mastery;
        if (subjectId != null) {
            List<Map<String, Object>> masteryList = knowledgeMasteryService.calculateMastery(studentId, subjectId);
            mastery = Map.of("studentId", studentId, "subjectId", subjectId, "knowledgePoints", masteryList);
        } else {
            mastery = knowledgeMasteryService.getMasteryByStudent(studentId);
        }
        return R.ok(mastery);
    }

    /**
     * 学情预警
     *
     * @param studentId 学生ID
     * @return 预警列表
     */
    @Operation(summary = "学情预警")
    @SaCheckLogin
    @GetMapping("/student/{studentId}/warnings")
    public R<List<Map<String, Object>>> getWarnings(@PathVariable Long studentId) {
        List<Map<String, Object>> warnings = warningService.getWarnings(studentId);
        return R.ok(warnings);
    }

    /**
     * 学情快照历史
     *
     * @param studentId 学生ID
     * @return 快照列表
     */
    @Operation(summary = "学情快照历史")
    @SaCheckLogin
    @GetMapping("/student/{studentId}/snapshots")
    public R<List<StudentProfileSnapshot>> getSnapshots(@PathVariable Long studentId) {
        List<StudentProfileSnapshot> snapshots = snapshotService.getSnapshots(studentId);
        return R.ok(snapshots);
    }
}
