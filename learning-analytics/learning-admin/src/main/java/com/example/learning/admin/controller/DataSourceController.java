package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.DataSource;
import com.example.learning.service.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据源管理Controller
 */
@Tag(name = "数据源管理")
@RestController
@RequestMapping("/api/v1/data-sources")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceService dataSourceService;

    /**
     * 查询数据源列表
     *
     * @return 数据源列表
     */
    @Operation(summary = "数据源列表")
    @SaCheckLogin
    @GetMapping
    public R<List<DataSource>> getList() {
        List<DataSource> list = dataSourceService.getList();
        return R.ok(list);
    }

    /**
     * 获取数据源详情
     *
     * @param id 数据源ID
     * @return 数据源详情
     */
    @Operation(summary = "数据源详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<DataSource> getById(@PathVariable Long id) {
        DataSource dataSource = dataSourceService.getById(id);
        return R.ok(dataSource);
    }

    /**
     * 创建数据源
     *
     * @param dataSource 数据源信息
     * @return 创建结果
     */
    @Operation(summary = "创建数据源")
    @SaCheckLogin
    @PostMapping
    public R<DataSource> create(@RequestBody DataSource dataSource) {
        DataSource created = dataSourceService.create(dataSource);
        return R.ok(created);
    }

    /**
     * 更新数据源
     *
     * @param id         数据源ID
     * @param dataSource 数据源信息
     * @return 更新结果
     */
    @Operation(summary = "更新数据源")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<DataSource> update(@PathVariable Long id, @RequestBody DataSource dataSource) {
        DataSource updated = dataSourceService.update(id, dataSource);
        return R.ok(updated);
    }

    /**
     * 删除数据源
     *
     * @param id 数据源ID
     * @return 操作结果
     */
    @Operation(summary = "删除数据源")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dataSourceService.delete(id);
        return R.ok();
    }

    /**
     * 手动触发同步
     *
     * @param id 数据源ID
     * @return 操作结果
     */
    @Operation(summary = "手动触发同步")
    @SaCheckLogin
    @PostMapping("/{id}/sync")
    public R<Void> triggerSync(@PathVariable Long id) {
        dataSourceService.triggerSync(id);
        return R.ok();
    }

    /**
     * 获取同步状态
     *
     * @param id 数据源ID
     * @return 同步状态信息
     */
    @Operation(summary = "同步状态")
    @SaCheckLogin
    @GetMapping("/{id}/sync-status")
    public R<Map<String, Object>> getSyncStatus(@PathVariable Long id) {
        Map<String, Object> status = dataSourceService.getSyncStatus(id);
        return R.ok(status);
    }
}
