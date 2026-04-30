package com.example.learning.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学校信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDTO {

    /**
     * 学校编码
     */
    private String schoolCode;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 学校类型
     */
    private String schoolType;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;
}
