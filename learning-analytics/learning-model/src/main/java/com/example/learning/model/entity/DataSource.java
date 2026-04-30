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
 * DataSource 实体类
 * 对应数据库表: data_source
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("data_source")
public class DataSource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String sourceName;

    private String sourceType;

    private String apiEndpoint;

    private String authConfig;

    private String syncConfig;

    private LocalDateTime lastSyncTime;

    private Integer syncStatus;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
