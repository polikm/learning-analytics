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
 * Report 实体类
 * 对应数据库表: report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("report")
public class Report {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String reportName;

    private String reportType;

    private Long templateId;

    private String sourceId;

    private String targetType;

    private String targetId;

    private String scopeIds;

    private Integer generateStatus;

    private String fileUrl;

    private Long fileSize;

    private String errorMsg;

    private Long generatedBy;

    private LocalDateTime generatedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
