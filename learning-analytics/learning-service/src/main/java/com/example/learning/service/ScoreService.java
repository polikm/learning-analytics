package com.example.learning.service;

import com.example.learning.model.vo.ClassRankingVO;
import com.example.learning.model.vo.ExamStatisticsVO;
import com.example.learning.model.vo.QuestionAnalysisVO;
import com.example.learning.model.vo.StudentTrendVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 成绩服务接口
 */
public interface ScoreService {

    /**
     * 测评统计分析（平均分/最高分/最低分/及格率/分数段分布）
     *
     * @param examId 测评ID
     * @return 统计结果
     */
    ExamStatisticsVO getStatistics(Long examId);

    /**
     * 题目分析（每题正确率/区分度）
     *
     * @param examId 测评ID
     * @return 题目分析列表
     */
    List<QuestionAnalysisVO> getQuestionAnalysis(Long examId);

    /**
     * 学生成绩趋势
     *
     * @param studentId 学生ID
     * @param subjectId 学科ID
     * @return 成绩趋势列表
     */
    List<StudentTrendVO> getStudentTrend(Long studentId, Long subjectId);

    /**
     * 班级排名
     *
     * @param classId 班级ID
     * @param examId  测评ID
     * @return 排名列表
     */
    List<ClassRankingVO> getClassRanking(Long classId, Long examId);

    /**
     * 线下成绩批量导入（解析Excel）
     *
     * @param examId 测评ID
     * @param file   Excel文件
     */
    void importOfflineScores(Long examId, MultipartFile file);
}
