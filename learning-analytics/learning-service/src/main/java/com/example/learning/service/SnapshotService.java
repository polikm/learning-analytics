package com.example.learning.service;

import com.example.learning.model.entity.StudentProfileSnapshot;

import java.util.List;

/**
 * 学情快照服务接口
 */
public interface SnapshotService {

    /**
     * 生成学情快照
     *
     * @param studentId 学生ID
     * @return 生成的快照
     */
    StudentProfileSnapshot generateSnapshot(Long studentId);

    /**
     * 批量生成班级学情快照
     *
     * @param classId 班级ID
     * @return 生成的快照数量
     */
    int generateClassSnapshots(Long classId);

    /**
     * 查询学情快照历史
     *
     * @param studentId 学生ID
     * @return 快照列表
     */
    List<StudentProfileSnapshot> getSnapshots(Long studentId);
}
