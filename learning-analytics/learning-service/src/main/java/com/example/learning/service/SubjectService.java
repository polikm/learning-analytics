package com.example.learning.service;

import com.example.learning.model.entity.Subject;

import java.util.List;

/**
 * 学科服务接口
 */
public interface SubjectService {

    /**
     * 查询学科列表
     *
     * @param tenantId 租户ID
     * @return 学科列表
     */
    List<Subject> getList(Long tenantId);

    /**
     * 创建学科
     *
     * @param subject 学科信息
     * @return 创建的学科
     */
    Subject create(Subject subject);

    /**
     * 更新学科
     *
     * @param id      学科ID
     * @param subject 学科信息
     * @return 更新后的学科
     */
    Subject update(Long id, Subject subject);

    /**
     * 删除学科
     *
     * @param id 学科ID
     */
    void delete(Long id);
}
