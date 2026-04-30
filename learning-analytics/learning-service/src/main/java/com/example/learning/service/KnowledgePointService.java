package com.example.learning.service;

import com.example.learning.model.entity.KnowledgePoint;

import java.util.List;

/**
 * 知识点服务接口
 */
public interface KnowledgePointService {

    /**
     * 查询知识点树（递归构建树形结构）
     *
     * @param subjectId 学科ID
     * @return 知识点树形列表
     */
    List<KnowledgePoint> getTree(Long subjectId);

    /**
     * 创建知识点
     *
     * @param knowledgePoint 知识点信息
     * @return 创建的知识点
     */
    KnowledgePoint create(KnowledgePoint knowledgePoint);

    /**
     * 更新知识点
     *
     * @param id             知识点ID
     * @param knowledgePoint 知识点信息
     * @return 更新后的知识点
     */
    KnowledgePoint update(Long id, KnowledgePoint knowledgePoint);

    /**
     * 删除知识点
     *
     * @param id 知识点ID
     */
    void delete(Long id);
}
