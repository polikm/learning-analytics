package com.example.learning.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SysMessage 实体类
 * 对应数据库表: sys_message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_message")
public class SysMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long senderId;

    private Long receiverId;

    private String title;

    private String content;

    private String msgType;

    @TableField("is_read")
    private Integer isRead;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
