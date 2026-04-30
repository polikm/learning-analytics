package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Question;

/**
 * 题目服务接口
 */
public interface QuestionService {

    /**
     * 多条件分页查询题目
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
    Page<Question> getPage(PageQueryDTO dto, Long subjectId, Integer questionType,
                           Integer difficulty, Long knowledgeId, Integer reviewStatus, String keyword);

    /**
     * 获取题目详情
     *
     * @param id 题目ID
     * @return 题目实体
     */
    Question getById(Long id);

    /**
     * 创建题目
     *
     * @param question 题目信息
     * @return 创建的题目
     */
    Question create(Question question);

    /**
     * 更新题目
     *
     * @param id       题目ID
     * @param question 题目信息
     * @return 更新后的题目
     */
    Question update(Long id, Question question);

    /**
     * 删除题目
     *
     * @param id 题目ID
     */
    void delete(Long id);

    /**
     * 审核题目
     *
     * @param id           题目ID
     * @param reviewStatus 审核状态
     */
    void review(Long id, Integer reviewStatus);
}
