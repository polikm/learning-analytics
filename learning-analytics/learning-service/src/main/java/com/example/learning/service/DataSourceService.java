package com.example.learning.service;

import com.example.learning.model.entity.DataSource;

import java.util.List;
import java.util.Map;

/**
 * 数据源管理服务接口
 */
public interface DataSourceService {

    /**
     * 查询数据源列表
     *
     * @return 数据源列表
     */
    List<DataSource> getList();

    /**
     * 获取数据源详情
     *
     * @param id 数据源ID
     * @return 数据源详情
     */
    DataSource getById(Long id);

    /**
     * 创建数据源
     *
     * @param dataSource 数据源信息
     * @return 创建后的数据源
     */
    DataSource create(DataSource dataSource);

    /**
     * 更新数据源
     *
     * @param id         数据源ID
     * @param dataSource 数据源信息
     * @return 更新后的数据源
     */
    DataSource update(Long id, DataSource dataSource);

    /**
     * 删除数据源
     *
     * @param id 数据源ID
     */
    void delete(Long id);

    /**
     * 手动触发同步
     *
     * @param id 数据源ID
     */
    void triggerSync(Long id);

    /**
     * 获取同步状态
     *
     * @param id 数据源ID
     * @return 同步状态信息
     */
    Map<String, Object> getSyncStatus(Long id);
}
