package com.example.learning.service;

import com.example.learning.common.result.PageResult;
import com.example.learning.model.entity.SysDict;
import com.example.learning.model.entity.SysDictItem;

import java.util.List;

/**
 * 字典服务接口
 */
public interface DictService {

    /**
     * 分页查询字典列表
     *
     * @param keyword  搜索关键词
     * @param page     页码
     * @param pageSize 每页大小
     * @return 分页字典列表
     */
    PageResult<SysDict> getDictPage(String keyword, int page, int pageSize);

    /**
     * 获取字典详情（包含字典项列表）
     *
     * @param id 字典ID
     * @return 字典详情
     */
    SysDict getDictDetail(Long id);

    /**
     * 按字典编码查询字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<SysDictItem> getDictList(String dictCode);

    /**
     * 创建字典
     *
     * @param dict 字典实体
     * @return 创建的字典实体
     */
    SysDict createDict(SysDict dict);

    /**
     * 更新字典
     *
     * @param id   字典ID
     * @param dict 字典实体
     * @return 更新后的字典实体
     */
    SysDict updateDict(Long id, SysDict dict);

    /**
     * 删除字典
     *
     * @param id 字典ID
     */
    void deleteDict(Long id);

    /**
     * 创建字典项
     *
     * @param item 字典项实体
     * @return 创建的字典项实体
     */
    SysDictItem createDictItem(SysDictItem item);

    /**
     * 更新字典项
     *
     * @param id   字典项ID
     * @param item 字典项实体
     * @return 更新后的字典项实体
     */
    SysDictItem updateDictItem(Long id, SysDictItem item);

    /**
     * 删除字典项
     *
     * @param id 字典项ID
     */
    void deleteDictItem(Long id);
}
