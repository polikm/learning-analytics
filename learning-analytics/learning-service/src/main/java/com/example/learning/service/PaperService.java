package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Paper;
import com.example.learning.model.entity.PaperQuestion;
import com.example.learning.model.vo.PaperDetailVO;

import java.util.List;

/**
 * 试卷服务接口
 */
public interface PaperService {

    /**
     * 分页查询试卷
     *
     * @param dto 分页参数
     * @return 分页结果
     */
    Page<Paper> getPage(PageQueryDTO dto);

    /**
     * 获取试卷详情（含题目列表）
     *
     * @param id 试卷ID
     * @return 试卷详情
     */
    PaperDetailVO getById(Long id);

    /**
     * 创建试卷及题目关联
     *
     * @param paper     试卷信息
     * @param questions 题目关联列表
     * @return 创建的试卷
     */
    Paper create(Paper paper, List<PaperQuestion> questions);

    /**
     * 更新试卷及题目关联
     *
     * @param id        试卷ID
     * @param paper     试卷信息
     * @param questions 题目关联列表
     * @return 更新后的试卷
     */
    Paper update(Long id, Paper paper, List<PaperQuestion> questions);

    /**
     * 删除试卷
     *
     * @param id 试卷ID
     */
    void delete(Long id);

    /**
     * 智能组卷（按条件从题库随机抽题）
     *
     * @param subjectId    学科ID
     * @param knowledgeIds 知识点ID（逗号分隔）
     * @param difficulty   难度
     * @param typeRatio    题型比例（key为题型，value为数量）
     * @param totalCount   总题目数
     * @return 生成的试卷
     */
    Paper autoGenerate(Long subjectId, String knowledgeIds, Integer difficulty,
                       java.util.Map<Integer, Integer> typeRatio, Integer totalCount);
}
