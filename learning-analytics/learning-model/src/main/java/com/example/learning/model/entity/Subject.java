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
 * Subject 实体类
 * 对应数据库表: subject
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("subject")
public class Subject {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String subjectCode;

    private String subjectName;

    private Integer sortOrder;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
