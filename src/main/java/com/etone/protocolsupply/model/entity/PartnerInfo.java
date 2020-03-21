package com.etone.protocolsupply.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long    partnerId;

    /**
     * 公司名称
     */
    @Column(name = "COMPANYNO", length = 200)
    private String  companyNo;

    /**
     * 法人代表
     */
    @Column(name = "INCORPORATOR", length = 200)
    private String  incorporator;

    /**
     * 公司成立时间
     */
    @Column(name = "FOUNDED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date foundedDate;

    /**
     * 法定注册时间
     */
    @Column(name = "REGISTER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date RegisterDate;

    /**
     * 往来邀请函地址
     */
    @Column(name = "ADDRESS", length = 200)
    private String address;

    /**
     * 邮编
     */
    @Column(name = "ZIP", length = 100)
    private String  zip;

    /**
     * 单位简介
     */
    @Column(name = "INTRODUCE", length = 2000)
    private String  introduce;

    /**
     * 经营范围
     */
    @Column(name = "BUSIAREA", length = 2000)
    private String  busiArea;

    /**
     * 经营类型
     */
    @Column(name = "SUP_TYPE", length = 4)
    private Integer supType;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

}
