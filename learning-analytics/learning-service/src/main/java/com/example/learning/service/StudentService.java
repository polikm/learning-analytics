package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.StudentProfile;

import java.util.List;

/**
 * 学生服务接口
 */
public interface StudentService {

    /**
     * 分页查询学生列表
     *
     * @param dto 分页查询DTO
     * @return 分页结果
     */
    Page<StudentProfile> getPage(PageQueryDTO dto);

    /**
     * 获取学生详情
     *
     * @param id 学生ID
     * @return 学生档案实体
     */
    StudentProfile getById(Long id);

    /**
     * 按班级查询学生列表
     *
     * @param classId 班级ID
     * @return 学生列表
     */
    List<StudentProfile> getByClassId(Long classId);

    /**
     * 创建学生
     *
     * @param studentProfile 学生档案实体
     * @return 创建的学生档案实体
     */
    StudentProfile create(StudentProfile studentProfile);

    /**
     * 更新学生
     *
     * @param id             学生ID
     * @param studentProfile 学生档案实体
     * @return 更新后的学生档案实体
     */
    StudentProfile update(Long id, StudentProfile studentProfile);

    /**
     * 删除学生
     *
     * @param id 学生ID
     */
    void delete(Long id);
}
