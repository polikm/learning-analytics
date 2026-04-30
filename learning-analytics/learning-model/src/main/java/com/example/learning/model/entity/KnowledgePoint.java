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
 * KnowledgePoint 实体类
 * 对应数据库表: knowledge_point
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("knowledge_point")
public class KnowledgePoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long parentId;

    private Long subjectId;

    private String kpCode;

    private String kpName;

    private Integer level;

    private Integer sortOrder;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
