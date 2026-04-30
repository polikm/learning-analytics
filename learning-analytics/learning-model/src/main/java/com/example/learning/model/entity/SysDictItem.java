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
 * SysDictItem 实体类
 * 对应数据库表: sys_dict_item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_dict_item")
public class SysDictItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dictId;

    private String itemCode;

    private String itemValue;

    private String itemLabel;

    private Integer sortOrder;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
