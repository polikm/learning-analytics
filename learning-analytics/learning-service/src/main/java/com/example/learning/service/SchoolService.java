package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.dto.SchoolDTO;
import com.example.learning.model.entity.School;

/**
 * 学校服务接口
 */
public interface SchoolService {

    /**
     * 分页查询学校列表
     *
     * @param dto 分页查询DTO
     * @return 分页结果
     */
    Page<School> getPage(PageQueryDTO dto);

    /**
     * 获取学校详情
     *
     * @param id 学校ID
     * @return 学校实体
     */
    School getById(Long id);

    /**
     * 创建学校
     *
     * @param dto 学校信息DTO
     * @return 创建的学校实体
     */
    School create(SchoolDTO dto);

    /**
     * 更新学校
     *
     * @param id  学校ID
     * @param dto 学校信息DTO
     * @return 更新后的学校实体
     */
    School update(Long id, SchoolDTO dto);

    /**
     * 删除学校
     *
     * @param id 学校ID
     */
    void delete(Long id);
}
