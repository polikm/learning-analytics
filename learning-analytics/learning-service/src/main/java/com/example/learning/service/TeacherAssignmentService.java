package com.example.learning.service;

import com.example.learning.model.entity.TeacherAssignment;

import java.util.List;

/**
 * 教师任课分配服务接口
 */
public interface TeacherAssignmentService {

    /**
     * 查询任课分配列表
     *
     * @param teacherId   教师ID
     * @param schoolId    学校ID
     * @param subjectId   学科ID
     * @param academicYear 学年
     * @return 任课分配列表
     */
    List<TeacherAssignment> getList(Long teacherId, Long schoolId, Long subjectId, String academicYear);

    /**
     * 新增任课分配
     *
     * @param assignment 任课分配实体
     * @return 创建的任课分配实体
     */
    TeacherAssignment create(TeacherAssignment assignment);

    /**
     * 更新任课分配
     *
     * @param id         任课分配ID
     * @param assignment 任课分配实体
     * @return 更新后的任课分配实体
     */
    TeacherAssignment update(Long id, TeacherAssignment assignment);

    /**
     * 删除任课分配
     *
     * @param id 任课分配ID
     */
    void delete(Long id);

    /**
     * 获取教师的任课信息
     *
     * @param teacherId 教师ID
     * @return 任课分配列表
     */
    List<TeacherAssignment> getByTeacher(Long teacherId);
}
