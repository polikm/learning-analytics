package com.example.learning.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaperQuestion 实体类
 * 对应数据库表: paper_question
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("paper_question")
public class PaperQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long paperId;

    private Long questionId;

    private Integer sectionIndex;

    private Integer questionSeq;

    private BigDecimal score;

    private Integer sortOrder;
}
