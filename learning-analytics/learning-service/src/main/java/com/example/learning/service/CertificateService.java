package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Certificate;

import java.util.Map;

/**
 * 证书管理服务接口
 */
public interface CertificateService {

    /**
     * 分页查询证书列表
     *
     * @param dto       分页参数
     * @param studentId 学生ID（可选）
     * @param certType  证书类型（可选）
     * @return 分页结果
     */
    Page<Certificate> getPage(PageQueryDTO dto, Long studentId, String certType);

    /**
     * 获取证书详情
     *
     * @param id 证书ID
     * @return 证书详情
     */
    Certificate getById(Long id);

    /**
     * 创建证书
     *
     * @param certificate 证书信息
     * @return 创建后的证书
     */
    Certificate create(Certificate certificate);

    /**
     * 更新证书
     *
     * @param id          证书ID
     * @param certificate 证书信息
     * @return 更新后的证书
     */
    Certificate update(Long id, Certificate certificate);

    /**
     * 删除证书
     *
     * @param id 证书ID
     */
    void delete(Long id);

    /**
     * 审核证书
     *
     * @param id           证书ID
     * @param reviewStatus 审核状态
     * @param reviewRemark 审核备注
     */
    void review(Long id, Integer reviewStatus, String reviewRemark);

    /**
     * 获取证书统计信息
     *
     * @param tenantId 租户ID
     * @param schoolId 学校ID（可选）
     * @return 统计数据（各类型证书数量、获奖等级分布）
     */
    Map<String, Object> getStatistics(Long tenantId, Long schoolId);
}
