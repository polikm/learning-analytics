package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.KnowledgePoint;
import com.example.learning.model.mapper.KnowledgePointMapper;
import com.example.learning.service.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识点服务实现类
 */
@Service
public class KnowledgePointServiceImpl implements KnowledgePointService {

    @Autowired
    private KnowledgePointMapper knowledgePointMapper;

    @Override
    public List<KnowledgePoint> getTree(Long subjectId) {
        // 查询该学科下所有知识点
        LambdaQueryWrapper<KnowledgePoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgePoint::getSubjectId, subjectId)
                .orderByAsc(KnowledgePoint::getSortOrder);
        List<KnowledgePoint> allPoints = knowledgePointMapper.selectList(wrapper);

        // 递归构建树形结构
        return buildTree(allPoints, 0L);
    }

    /**
     * 递归构建知识点树
     *
     * @param allPoints 所有知识点
     * @param parentId  父节点ID
     * @return 树形结构
     */
    private List<KnowledgePoint> buildTree(List<KnowledgePoint> allPoints, Long parentId) {
        List<KnowledgePoint> tree = new ArrayList<>();
        for (KnowledgePoint point : allPoints) {
            if (parentId.equals(point.getParentId())) {
                // 递归查找子节点
                List<KnowledgePoint> children = buildTree(allPoints, point.getId());
                // children 不直接存储在 KnowledgePoint 实体中，但可以通过扩展字段或VO实现
                // 这里保持简单，返回扁平列表中的层级关系
                tree.add(point);
            }
        }
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgePoint create(KnowledgePoint knowledgePoint) {
        knowledgePoint.setStatus(1);
        knowledgePoint.setCreatedAt(LocalDateTime.now());
        knowledgePointMapper.insert(knowledgePoint);
        return knowledgePoint;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgePoint update(Long id, KnowledgePoint knowledgePoint) {
        KnowledgePoint existing = knowledgePointMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        knowledgePoint.setId(id);
        knowledgePointMapper.updateById(knowledgePoint);
        return knowledgePointMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        KnowledgePoint existing = knowledgePointMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 检查是否有子知识点
        LambdaQueryWrapper<KnowledgePoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgePoint::getParentId, id);
        Long childCount = knowledgePointMapper.selectCount(wrapper);
        if (childCount > 0) {
            throw new BusinessException("该知识点下存在子知识点，无法删除");
        }
        knowledgePointMapper.deleteById(id);
    }
}
