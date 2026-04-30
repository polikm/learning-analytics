package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.SysDict;
import com.example.learning.model.entity.SysDictItem;
import com.example.learning.model.mapper.SysDictItemMapper;
import com.example.learning.model.mapper.SysDictMapper;
import com.example.learning.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public PageResult<SysDict> getDictPage(String keyword, int page, int pageSize) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysDict::getDictName, keyword)
                    .or()
                    .like(SysDict::getDictCode, keyword);
        }
        wrapper.orderByDesc(SysDict::getCreatedAt);

        Page<SysDict> pageParam = new Page<>(page, pageSize);
        Page<SysDict> result = sysDictMapper.selectPage(pageParam, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public SysDict getDictDetail(Long id) {
        SysDict dict = sysDictMapper.selectById(id);
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return dict;
    }

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
    public SysDict updateDict(Long id, SysDict dict) {
        SysDict existing = sysDictMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 如果修改了字典编码，检查新编码是否已存在
        if (StringUtils.hasText(dict.getDictCode()) && !dict.getDictCode().equals(existing.getDictCode())) {
            Long count = sysDictMapper.selectCount(
                    new LambdaQueryWrapper<SysDict>()
                            .eq(SysDict::getDictCode, dict.getDictCode())
                            .ne(SysDict::getId, id)
            );
            if (count > 0) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
            }
        }

        dict.setId(id);
        sysDictMapper.updateById(dict);
        return sysDictMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDict(Long id) {
        SysDict existing = sysDictMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 删除字典下的所有字典项
        sysDictItemMapper.delete(
                new LambdaQueryWrapper<SysDictItem>()
                        .eq(SysDictItem::getDictId, id)
        );

        // 删除字典
        sysDictMapper.deleteById(id);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDictItem updateDictItem(Long id, SysDictItem item) {
        SysDictItem existing = sysDictItemMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        item.setId(id);
        sysDictItemMapper.updateById(item);
        return sysDictItemMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictItem(Long id) {
        SysDictItem existing = sysDictItemMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        sysDictItemMapper.deleteById(id);
    }
}
