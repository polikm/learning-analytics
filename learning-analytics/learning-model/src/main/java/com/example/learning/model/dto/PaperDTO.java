package com.example.learning.model.dto;

import com.example.learning.model.entity.Paper;
import com.example.learning.model.entity.PaperQuestion;
import lombok.Data;

import java.util.List;

/**
 * 试卷创建/更新DTO（含题目关联）
 */
@Data
public class PaperDTO {

    /**
     * 试卷基本信息
     */
    private Paper paper;

    /**
     * 题目关联列表
     */
    private List<PaperQuestion> questions;
}
