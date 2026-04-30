package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.SysDict;
import com.example.learning.model.entity.SysDictItem;
import com.example.learning.service.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public R<Void> createDict(@Valid @RequestBody SysDict dict) {
        dictService.createDict(dict);
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
    public R<Void> createDictItem(@Valid @RequestBody SysDictItem item) {
        dictService.createDictItem(item);
        return R.ok();
    }
}
