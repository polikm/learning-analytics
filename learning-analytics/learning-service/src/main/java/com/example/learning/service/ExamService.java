package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Exam;

/**
 * 测评服务接口
 */
public interface ExamService {

    /**
     * 分页查询测评
     *
     * @param dto 分页参数
     * @return 分页结果
     */
    Page<Exam> getPage(PageQueryDTO dto);

    /**
     * 获取测评详情
     *
     * @param id 测评ID
     * @return 测评实体
     */
    Exam getById(Long id);

    /**
     * 创建测评
     *
     * @param exam 测评信息
     * @return 创建的测评
     */
    Exam create(Exam exam);

    /**
     * 更新测评
     *
     * @param id   测评ID
     * @param exam 测评信息
     * @return 更新后的测评
     */
    Exam update(Long id, Exam exam);

    /**
     * 删除测评
     *
     * @param id 测评ID
     */
    void delete(Long id);

    /**
     * 发布测评（状态改为进行中）
     *
     * @param id 测评ID
     */
    void publish(Long id);
}
