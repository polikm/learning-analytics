package com.example.learning.service;

import com.example.learning.model.entity.SysDict;
import com.example.learning.model.entity.SysDictItem;

import java.util.List;

/**
 * 字典服务接口
 */
public interface DictService {

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
     * 创建字典项
     *
     * @param item 字典项实体
     * @return 创建的字典项实体
     */
    SysDictItem createDictItem(SysDictItem item);
}
