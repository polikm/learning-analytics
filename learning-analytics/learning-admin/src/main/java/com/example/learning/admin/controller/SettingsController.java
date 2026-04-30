package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.SysSetting;
import com.example.learning.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统设置Controller
 */
@Tag(name = "系统设置")
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingService settingService;

    /**
     * 获取所有设置
     *
     * @return 设置列表
     */
    @Operation(summary = "获取所有设置")
    @SaCheckLogin
    @GetMapping
    public R<List<SysSetting>> getAll() {
        List<SysSetting> settings = settingService.getAll();
        return R.ok(settings);
    }

    /**
     * 根据键获取设置值
     *
     * @param key 设置键
     * @return 设置值
     */
    @Operation(summary = "根据键获取设置值")
    @SaCheckLogin
    @GetMapping("/{key}")
    public R<String> getByKey(@PathVariable String key) {
        String value = settingService.getByKey(key);
        return R.ok(value);
    }

    /**
     * 批量更新设置
     *
     * @param settings 设置键值对
     * @return 操作结果
     */
    @Operation(summary = "批量更新设置")
    @SaCheckLogin
    @PutMapping
    public R<Void> batchUpdate(@RequestBody Map<String, String> settings) {
        settingService.batchUpdate(settings);
        return R.ok();
    }

    /**
     * 更新单个设置
     *
     * @param key   设置键
     * @param body  设置值
     * @return 操作结果
     */
    @Operation(summary = "更新单个设置")
    @SaCheckLogin
    @PutMapping("/{key}")
    public R<Void> update(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        settingService.update(key, value);
        return R.ok();
    }
}
