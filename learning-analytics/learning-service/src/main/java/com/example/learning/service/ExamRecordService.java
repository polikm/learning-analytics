package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.ExamRecord;

/**
 * 测评记录服务接口
 */
public interface ExamRecordService {

    /**
     * 开始答题（创建ExamRecord）
     *
     * @param examId    测评ID
     * @param studentId 学生ID
     * @return 答题记录
     */
    ExamRecord startExam(Long examId, Long studentId);

    /**
     * 交卷
     *
     * @param examId    测评ID
     * @param studentId 学生ID
     */
    void submitExam(Long examId, Long studentId);

    /**
     * 查询答题记录
     *
     * @param examId    测评ID
     * @param studentId 学生ID
     * @return 答题记录
     */
    ExamRecord getByExamAndStudent(Long examId, Long studentId);

    /**
     * 按测评查询成绩列表
     *
     * @param examId 测评ID
     * @param dto    分页参数
     * @return 分页结果
     */
    Page<ExamRecord> getPageByExam(Long examId, PageQueryDTO dto);
}
