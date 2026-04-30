package com.example.learning.service;

import com.example.learning.model.entity.ParentStudent;

import java.util.List;

/**
 * 家长学生关联服务接口
 */
public interface ParentStudentService {

    /**
     * 查询关联列表
     *
     * @param parentId  家长ID
     * @param studentId 学生ID
     * @return 关联列表
     */
    List<ParentStudent> getList(Long parentId, Long studentId);

    /**
     * 新增关联
     *
     * @param ps 家长学生关联实体
     * @return 创建的关联实体
     */
    ParentStudent create(ParentStudent ps);

    /**
     * 更新关联
     *
     * @param id 关联ID
     * @param ps 家长学生关联实体
     * @return 更新后的关联实体
     */
    ParentStudent update(Long id, ParentStudent ps);

    /**
     * 删除关联
     *
     * @param id 关联ID
     */
    void delete(Long id);

    /**
     * 获取家长关联的学生列表
     *
     * @param parentId 家长ID
     * @return 关联列表
     */
    List<ParentStudent> getByParent(Long parentId);

    /**
     * 获取学生关联的家长列表
     *
     * @param studentId 学生ID
     * @return 关联列表
     */
    List<ParentStudent> getByStudent(Long studentId);
}
