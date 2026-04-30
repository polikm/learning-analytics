package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.entity.SysSetting;
import com.example.learning.model.mapper.SysSettingMapper;
import com.example.learning.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统设置服务实现类
 */
@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private SysSettingMapper sysSettingMapper;

    @Override
    public List<SysSetting> getAll() {
        LambdaQueryWrapper<SysSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysSetting::getSettingGroup);
        return sysSettingMapper.selectList(wrapper);
    }

    @Override
    public List<SysSetting> getByGroup(String group) {
        LambdaQueryWrapper<SysSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSetting::getSettingGroup, group);
        wrapper.orderByAsc(SysSetting::getId);
        return sysSettingMapper.selectList(wrapper);
    }

    @Override
    public String getByKey(String key) {
        LambdaQueryWrapper<SysSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSetting::getSettingKey, key);
        SysSetting setting = sysSettingMapper.selectOne(wrapper);
        return setting != null ? setting.getSettingValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String key, String value) {
        LambdaUpdateWrapper<SysSetting> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysSetting::getSettingKey, key)
               .set(SysSetting::getSettingValue, value)
               .set(SysSetting::getUpdatedAt, LocalDateTime.now());
        int rows = sysSettingMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdate(Map<String, String> settings) {
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            LambdaUpdateWrapper<SysSetting> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(SysSetting::getSettingKey, entry.getKey())
                   .set(SysSetting::getSettingValue, entry.getValue())
                   .set(SysSetting::getUpdatedAt, now);
            sysSettingMapper.update(null, wrapper);
        }
    }
}
