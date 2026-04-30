package com.example.learning.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryDTO {

    /**
     * 当前页码，默认1
     */
    private Integer page = 1;

    /**
     * 每页大小，默认20
     */
    private Integer size = 20;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向
     */
    private String sortOrder;
}
