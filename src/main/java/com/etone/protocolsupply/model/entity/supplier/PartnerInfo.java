package com.etone.protocolsupply.model.entity.supplier;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    @Column(name = "BUSI_NATURE", length = 200)
    private String busiNature;

    /**
     * 行业分类
     */
    @Column(name = "TRADE_CATEGORY", length = 200)
    private String tradeCategory;

    /**
     * 主营产品或业务
     */
    @Column(name = "BUSINESS", length = 200)
    private String business;

    /**
     * 注册资金
     */
    @Column(name = "FUND", length = 100)
    private String fund;

    /**
     * 注册资金币种
     */
    @Column(name = "FUND_CURRENCY", length = 50)
    private String fundCurrency;

    /**
     * 注册资金单位
     */
    @Column(name = "FUND_UNIT", length = 20)
    private String fundUnit;

    /**
     * 企业人数
     */
    @Column(name = "BUSI_NUMBER", length = 20)
    private String busiNumber;

    /**
     * 法人代表姓名
     */
    @Column(name = "INCORPORATOR", length = 200)
    private String incorporator;

    /**
     * 证件号码
     */
    @Column(name = "IDENTIFICATION", length = 20)
    private String identification;

    /**
     * 详细地址
     */
    @Column(name = "DETAIL_ADDRESS", length = 2000)
    private String detailAddress;

    /**
     * 机构注册地（1：境内；2：境外）
     */
    @Column(name = "DOMICILE", length = 4)
    private Integer domicile;

    /**
     * 公司所在地
     */
    @Column(name = "ADDRESS", length = 500)
    private String address;

    /**
     * 邮政编码
     */
    @Column(name = "ZIP", length = 100)
    private String zip;

    /**
     * 公司网址
     */
    @Column(name = "WEBSITE", length = 200)
    private String website;

    /**
     * 企业类别（1：监狱企业；2：残疾人企业；3：中小微企业）
     */
    @Column(name = "BUSI_TYPE", length = 4)
    private Integer busiType;

    /**
     * 公司简介
     */
    @Column(name = "INTRODUCE", length = 2000)
    private String introduce;

    /**
     * 认证状态 1已认证   2未认证
     */
    @Column(name = "AUTH_STATUS", length = 4)
    private Integer authStatus;

    /**
     * 认证方式
     */
    @Column(name = "AUTH_METHOD", length = 50)
    private String authMethod;

    /**
     * 认证时间
     */
    @Column(name = "AUTH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date authDate;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

}
