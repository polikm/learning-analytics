package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.DataSource;
import com.example.learning.model.mapper.DataSourceMapper;
import com.example.learning.service.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源管理服务实现类
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

    private static final Logger log = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Override
    public List<DataSource> getList() {
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DataSource::getCreatedAt);
        return dataSourceMapper.selectList(wrapper);
    }

    @Override
    public DataSource getById(Long id) {
        DataSource dataSource = dataSourceMapper.selectById(id);
        if (dataSource == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return dataSource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataSource create(DataSource dataSource) {
        dataSource.setSyncStatus(0); // 未同步
        dataSource.setStatus(1); // 启用
        dataSource.setCreatedAt(LocalDateTime.now());
        dataSource.setUpdatedAt(LocalDateTime.now());
        dataSourceMapper.insert(dataSource);
        return dataSource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataSource update(Long id, DataSource dataSource) {
        DataSource existing = dataSourceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        dataSource.setId(id);
        dataSource.setUpdatedAt(LocalDateTime.now());
        dataSourceMapper.updateById(dataSource);
        return dataSourceMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DataSource existing = dataSourceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        dataSourceMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void triggerSync(Long id) {
        DataSource existing = dataSourceMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 更新同步状态为同步中
        DataSource update = new DataSource();
        update.setId(id);
        update.setSyncStatus(1); // 同步中
        update.setUpdatedAt(LocalDateTime.now());
        dataSourceMapper.updateById(update);

        // 异步执行同步
        asyncSyncData(id);
    }

    @Async
    public void asyncSyncData(Long dataSourceId) {
        try {
            DataSource dataSource = dataSourceMapper.selectById(dataSourceId);
            if (dataSource == null) {
                return;
            }

            // TODO: 实际项目中这里应调用对应数据源的API进行数据同步
            Thread.sleep(3000);

            // 更新同步状态为成功
            DataSource update = new DataSource();
            update.setId(dataSourceId);
            update.setSyncStatus(2); // 同步成功
            update.setLastSyncTime(LocalDateTime.now());
            update.setUpdatedAt(LocalDateTime.now());
            dataSourceMapper.updateById(update);
        } catch (Exception e) {
            log.error("数据同步失败, dataSourceId={}", dataSourceId, e);
            DataSource update = new DataSource();
            update.setId(dataSourceId);
            update.setSyncStatus(3); // 同步失败
            update.setUpdatedAt(LocalDateTime.now());
            dataSourceMapper.updateById(update);
        }
    }

    @Override
    public Map<String, Object> getSyncStatus(Long id) {
        DataSource dataSource = dataSourceMapper.selectById(id);
        if (dataSource == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", dataSource.getId());
        result.put("sourceName", dataSource.getSourceName());
        result.put("syncStatus", dataSource.getSyncStatus());
        result.put("syncStatusText", getSyncStatusText(dataSource.getSyncStatus()));
        result.put("lastSyncTime", dataSource.getLastSyncTime());
        return result;
    }

    private String getSyncStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "未同步";
            case 1: return "同步中";
            case 2: return "同步成功";
            case 3: return "同步失败";
            default: return "未知";
        }
    }
}
