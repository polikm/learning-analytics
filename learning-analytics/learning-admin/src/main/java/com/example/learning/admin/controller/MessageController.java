package com.example.learning.admin.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.result.PageResult;
import com.example.learning.common.result.R;
import com.example.learning.model.entity.SysMessage;
import com.example.learning.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统消息管理Controller
 */
@Tag(name = "系统消息管理")
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 分页查询消息列表
     *
     * @param receiverId  接收人ID（可选）
     * @param messageType 消息类型（可选）
     * @param isRead      是否已读（可选）
     * @param page        当前页码
     * @param pageSize    每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询消息列表")
    @SaCheckLogin
    @GetMapping
    public R<PageResult<SysMessage>> getPage(@RequestParam(required = false) Long receiverId,
                                              @RequestParam(required = false) String messageType,
                                              @RequestParam(required = false) Integer isRead,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysMessage> result = messageService.getPage(receiverId, messageType, isRead, page, pageSize);
        PageResult<SysMessage> pageResult = PageResult.of(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
        );
        return R.ok(pageResult);
    }

    /**
     * 获取未读消息数量
     *
     * @param receiverId 接收人ID
     * @return 未读数量
     */
    @Operation(summary = "获取未读消息数量")
    @SaCheckLogin
    @GetMapping("/unread-count")
    public R<Integer> getUnreadCount(@RequestParam Long receiverId) {
        int count = messageService.getUnreadCount(receiverId);
        return R.ok(count);
    }

    /**
     * 发送消息
     *
     * @param message 消息信息
     * @return 操作结果
     */
    @Operation(summary = "发送消息")
    @SaCheckLogin
    @PostMapping
    public R<Void> send(@RequestBody SysMessage message) {
        messageService.send(message);
        return R.ok();
    }

    /**
     * 标记消息已读
     *
     * @param id 消息ID
     * @return 操作结果
     */
    @Operation(summary = "标记消息已读")
    @SaCheckLogin
    @PutMapping("/{id}/read")
    public R<Void> markRead(@PathVariable Long id) {
        messageService.markRead(id);
        return R.ok();
    }

    /**
     * 全部标记已读
     *
     * @param receiverId 接收人ID
     * @return 操作结果
     */
    @Operation(summary = "全部标记已读")
    @SaCheckLogin
    @PutMapping("/read-all")
    public R<Void> markAllRead(@RequestParam Long receiverId) {
        messageService.markAllRead(receiverId);
        return R.ok();
    }

    /**
     * 删除消息
     *
     * @param id 消息ID
     * @return 操作结果
     */
    @Operation(summary = "删除消息")
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return R.ok();
    }
}
