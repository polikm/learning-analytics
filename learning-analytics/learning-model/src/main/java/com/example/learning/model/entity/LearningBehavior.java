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
 * LearningBehavior 实体类
 * 对应数据库表: learning_behavior
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("learning_behavior")
public class LearningBehavior {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学生用户ID */
    private Long studentId;

    /** 学科ID */
    private Long subjectId;

    /** 行为类型: login/logout/study/practice/exam/video/interaction */
    private String behaviorType;

    /** 行为详情(JSON) */
    private String behaviorDetail;

    /** 持续时间(秒) */
    private Integer duration;

    /** 来源: web/mobile/api */
    private String source;

    /** IP地址 */
    private String ipAddress;

    /** 用户代理 */
    private String userAgent;

    /** 行为时间 */
    private LocalDateTime behaviorTime;

    /** 租户ID */
    private Long tenantId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
