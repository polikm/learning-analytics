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
 * Certificate 实体类
 * 对应数据库表: certificate
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("certificate")
public class Certificate {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long studentId;

    private String certName;

    private String certType;

    private String awardLevel;

    private String awardGrade;

    private String issuingOrg;

    private String certNo;

    private LocalDateTime awardDate;

    private String certFileUrl;

    private String description;

    private Long submitterId;

    private Long reviewerId;

    private Integer reviewStatus;

    private String reviewRemark;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
