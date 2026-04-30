package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.SysDict;
import com.example.learning.model.entity.SysDictItem;
import com.example.learning.model.mapper.SysDictItemMapper;
import com.example.learning.model.mapper.SysDictMapper;
import com.example.learning.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典服务实现类
 */
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    @Override
    public List<SysDictItem> getDictList(String dictCode) {
        // 1. 根据字典编码查询字典
        SysDict dict = sysDictMapper.selectOne(
                new LambdaQueryWrapper<SysDict>()
                        .eq(SysDict::getDictCode, dictCode)
        );
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 2. 根据字典ID查询字典项列表，按排序字段升序
        return sysDictItemMapper.selectList(
                new LambdaQueryWrapper<SysDictItem>()
                        .eq(SysDictItem::getDictId, dict.getId())
                        .eq(SysDictItem::getStatus, 1)
                        .orderByAsc(SysDictItem::getSortOrder)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDict createDict(SysDict dict) {
        // 检查字典编码是否已存在
        Long count = sysDictMapper.selectCount(
                new LambdaQueryWrapper<SysDict>()
                        .eq(SysDict::getDictCode, dict.getDictCode())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
        }

        dict.setStatus(1);
        dict.setCreatedAt(LocalDateTime.now());
        sysDictMapper.insert(dict);
        return dict;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDictItem createDictItem(SysDictItem item) {
        // 检查关联的字典是否存在
        SysDict dict = sysDictMapper.selectById(item.getDictId());
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        item.setStatus(1);
        item.setCreatedAt(LocalDateTime.now());
        sysDictItemMapper.insert(item);
        return item;
    }
}
