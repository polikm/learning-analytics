package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.SysDict;
import com.example.learning.model.entity.SysDictItem;
import com.example.learning.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典管理Controller
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/api/v1/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    /**
     * 分页查询字典列表
     *
     * @param keyword  搜索关键词
     * @param page     页码
     * @param pageSize 每页大小
     * @return 分页字典列表
     */
    @Operation(summary = "分页查询字典列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<SysDict>> getDictPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysDict> result = dictService.getDictPage(keyword, page, pageSize);
        return R.ok(result);
    }

    /**
     * 字典详情（包含字典项列表）
     *
     * @param id 字典ID
     * @return 字典详情
     */
    @Operation(summary = "字典详情")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<Map<String, Object>> getDictDetail(@PathVariable Long id) {
        SysDict dict = dictService.getDictDetail(id);
        // 查询该字典下的所有字典项
        List<SysDictItem> items = dictService.getDictList(dict.getDictCode());
        Map<String, Object> data = new HashMap<>();
        data.put("dict", dict);
        data.put("items", items);
        return R.ok(data);
    }

    /**
     * 按字典编码查询字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    @Operation(summary = "按字典编码查询字典项列表")
    @SaCheckLogin
    @GetMapping("/items")
    public R<List<SysDictItem>> getDictItems(@RequestParam String dictCode) {
        List<SysDictItem> list = dictService.getDictList(dictCode);
        return R.ok(list);
    }

    /**
     * 创建字典
     *
     * @param dict 字典实体
     * @return 操作结果
     */
    @Operation(summary = "创建字典")
    @SaCheckLogin
    @PostMapping
    public R<SysDict> createDict(@Valid @RequestBody SysDict dict) {
        SysDict created = dictService.createDict(dict);
        return R.ok(created);
    }

    /**
     * 更新字典
     *
     * @param id   字典ID
     * @param dict 字典实体
     * @return 操作结果
     */
    @Operation(summary = "更新字典")
    @SaCheckLogin
    @PutMapping("/{id}")
    public R<SysDict> updateDict(@PathVariable Long id, @Valid @RequestBody SysDict dict) {
        SysDict updated = dictService.updateDict(id, dict);
        return R.ok(updated);
    }

    /**
     * 删除字典
     *
     * @param id 字典ID
     * @return 操作结果
     */
    @Operation(summary = "删除字典")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> deleteDict(@PathVariable Long id) {
        dictService.deleteDict(id);
        return R.ok();
    }

    /**
     * 创建字典项
     *
     * @param item 字典项实体
     * @return 操作结果
     */
    @Operation(summary = "创建字典项")
    @SaCheckLogin
    @PostMapping("/item")
    public R<SysDictItem> createDictItem(@Valid @RequestBody SysDictItem item) {
        SysDictItem created = dictService.createDictItem(item);
        return R.ok(created);
    }

    /**
     * 更新字典项
     *
     * @param id   字典项ID
     * @param item 字典项实体
     * @return 操作结果
     */
    @Operation(summary = "更新字典项")
    @SaCheckLogin
    @PutMapping("/item/{id}")
    public R<SysDictItem> updateDictItem(@PathVariable Long id, @Valid @RequestBody SysDictItem item) {
        SysDictItem updated = dictService.updateDictItem(id, item);
        return R.ok(updated);
    }

    /**
     * 删除字典项
     *
     * @param id 字典项ID
     * @return 操作结果
     */
    @Operation(summary = "删除字典项")
    @SaCheckLogin
    @DeleteMapping("/item/{id}")
    public R<Void> deleteDictItem(@PathVariable Long id) {
        dictService.deleteDictItem(id);
        return R.ok();
    }
}
