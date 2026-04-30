package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.entity.SysMessage;

/**
 * 系统消息服务接口
 */
public interface MessageService {

    /**
     * 分页查询消息列表
     *
     * @param receiverId  接收人ID
     * @param messageType 消息类型（可选）
     * @param isRead      是否已读（可选）
     * @param page        当前页码
     * @param size        每页大小
     * @return 分页结果
     */
    Page<SysMessage> getPage(Long receiverId, String messageType, Integer isRead, int page, int size);

    /**
     * 发送消息
     *
     * @param message 消息信息
     */
    void send(SysMessage message);

    /**
     * 标记消息已读
     *
     * @param id 消息ID
     */
    void markRead(Long id);

    /**
     * 全部标记已读
     *
     * @param receiverId 接收人ID
     */
    void markAllRead(Long receiverId);

    /**
     * 获取未读消息数量
     *
     * @param receiverId 接收人ID
     * @return 未读数量
     */
    int getUnreadCount(Long receiverId);

    /**
     * 删除消息
     *
     * @param id 消息ID
     */
    void delete(Long id);
}
