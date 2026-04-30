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
 * ParentStudent 实体类
 * 对应数据库表: parent_student
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("parent_student")
public class ParentStudent {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private Long studentId;

    private String relation;

    private Integer status;

    private String remark;

    private Long tenantId;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
