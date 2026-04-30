package com.example.learning.service;

import com.example.learning.model.entity.SysSetting;

import java.util.List;
import java.util.Map;

/**
 * 系统设置服务接口
 */
public interface SettingService {

    /**
     * 获取所有设置
     *
     * @return 设置列表
     */
    List<SysSetting> getAll();

    /**
     * 根据分组获取设置
     *
     * @param group 设置分组
     * @return 设置列表
     */
    List<SysSetting> getByGroup(String group);

    /**
     * 根据键获取设置值
     *
     * @param key 设置键
     * @return 设置值
     */
    String getByKey(String key);

    /**
     * 更新单个设置
     *
     * @param key   设置键
     * @param value 设置值
     */
    void update(String key, String value);

    /**
     * 批量更新设置
     *
     * @param settings 设置键值对
     */
    void batchUpdate(Map<String, String> settings);
}
