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
 * ReportTemplate 实体类
 * 对应数据库表: report_template
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("report_template")
public class ReportTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String templateName;

    private String templateType;

    private String templateConfig;

    @TableField("is_default")
    private Integer isDefault;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
