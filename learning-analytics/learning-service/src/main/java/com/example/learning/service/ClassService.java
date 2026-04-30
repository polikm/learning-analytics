package com.example.learning.service;

import com.example.learning.model.entity.Class;

import java.util.List;

/**
 * 班级服务接口
 */
public interface ClassService {

    /**
     * 按年级查询班级列表
     *
     * @param gradeId 年级ID
     * @return 班级列表
     */
    List<Class> getList(Long gradeId);

    /**
     * 创建班级
     *
     * @param clazz 班级实体
     * @return 创建的班级实体
     */
    Class create(Class clazz);

    /**
     * 更新班级
     *
     * @param id    班级ID
     * @param clazz 班级实体
     * @return 更新后的班级实体
     */
    Class update(Long id, Class clazz);

    /**
     * 删除班级
     *
     * @param id 班级ID
     */
    void delete(Long id);
}
