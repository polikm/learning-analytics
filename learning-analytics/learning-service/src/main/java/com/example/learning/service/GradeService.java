package com.example.learning.service;

import com.example.learning.model.entity.Grade;

import java.util.List;

/**
 * 年级服务接口
 */
public interface GradeService {

    /**
     * 按学校查询年级列表
     *
     * @param schoolId 学校ID
     * @return 年级列表
     */
    List<Grade> getList(Long schoolId);

    /**
     * 创建年级
     *
     * @param grade 年级实体
     * @return 创建的年级实体
     */
    Grade create(Grade grade);

    /**
     * 更新年级
     *
     * @param id    年级ID
     * @param grade 年级实体
     * @return 更新后的年级实体
     */
    Grade update(Long id, Grade grade);

    /**
     * 删除年级
     *
     * @param id 年级ID
     */
    void delete(Long id);
}
