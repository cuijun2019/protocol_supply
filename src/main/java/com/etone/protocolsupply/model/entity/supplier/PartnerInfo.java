package com.etone.protocolsupply.model.entity.supplier;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author huangzairong
 * @date 2020/3/22
 */
@Data
@Entity
@Table(name = "PARTNER_INFO")
public class PartnerInfo implements Serializable {

    /**
     * 供应商信息id（主键）
     */
    @Id
    @Column(name = "PARTNER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;

    /**
     * 交易主体类型（1：供应商；2：代理商）
     */
    @Column(name = "SUP_TYPE", length = 4)
    private Integer supType;

    /**
     * 单位名称
     */
    @Column(name = "COMPANY_NO", length = 200)
    private String companyNo;

    /**
     * 企业性质
     */
    private String busiNature;

    /**
     * 行业分类
     */
    private String tradeCategory;

    /**
     * 主营产品或业务
     */
    private String business;

    /**
     * 注册资金
     */
    private String fund;

    /**
     * 注册资金币种
     */
    private String fundCurrency;

    /**
     * 注册资金单位
     */
    private String fundUnit;

    /**
     * 企业人数
     */
    private String busiNumber;

    /**
     * 法人代表姓名
     */
    @Column(name = "INCORPORATOR", length = 200)
    private String incorporator;

    /**
     * 证件号码
     */
    private String identification;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 机构注册地（1：境内；2：境外）
     */
    private Integer domicile;

    /**
     * 公司所在地
     */
    @Column(name = "ADDRESS", length = 200)
    private String address;

    /**
     * 邮政编码
     */
    @Column(name = "ZIP", length = 100)
    private String zip;

    /**
     * 公司网址
     */
    private String website;

    /**
     * 企业类别（1：监狱企业；2：残疾人企业；3：中小微企业）
     */
    private Integer busiType;

    /**
     * 公司简介
     */
    @Column(name = "INTRODUCE", length = 2000)
    private String introduce;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

}
