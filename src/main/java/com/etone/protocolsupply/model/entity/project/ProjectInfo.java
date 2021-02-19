package com.etone.protocolsupply.model.entity.project;

import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "PROJECT_INFO")
//@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class ProjectInfo implements Serializable {

    @Id
    @Column(name = "PROJECT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    /**
     * 审核状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;
    /**
     * 询价id
     */
    @Column(name = "INQUIRY_ID", length = 20)
    private Long inquiryId;

    /**
     * 询价编号
     */
    @Column(name = "INQUIRY_CODE", length = 100)
    private String inquiryCode;

    /**
     * 项目编号
     */
    @Column(name = "PROJECT_CODE", length = 100)
    private String projectCode;

    /**
     * 项目预算
     */
    @Column(name = "PROJECT_BUDGET", length = 20)
    private Double projectBudget;
    /**
     * 采购人
     */
    @Column(name = "PURCHASER", length = 100)
    private String purchaser;

    /**
     * 经办人
     */
    @Column(name = "OPERATOR", length = 32)
    private String operator;

    /**
     * 经办人联系电话
     */
    @Column(name = "OPERATOR_NUMBER", length = 200)
    private String operator_number;

    /**
     * 制造商联系人
     */
    @Column(name = "PARTNER_CONTACT", length = 32)
    private String partner_contact;

    /**
     * 制造商联系人电话
     */
    @Column(name = "PARTNER_CONTACT_NUMBER", length = 50)
    private String partner_contact_number;

    /**
     * 产品联系人
     */
    @Column(name = "PRODUCT_CONTACT", length = 32)
    private String product_contact;

    /**
     * 产品联系人电话
     */
    @Column(name = "PRODUCT_CONTACT_NUMBER", length = 50)
    private String product_contact_number;


    /**
     * 项目主题
     */
    @Column(name = "PROJECT_SUBJECT", length = 1000)
    private String projectSubject;

    /**
     * 产品信息
     */
    @Column(name = "CARGONAME", length = 200)
    private String cargoName;

    /**
     * 项目委托单位
     */
    @Column(name = "projectentrustingunit", length = 200)
    private String projectEntrustingUnit;

    /**
     * 最终使用单位
     */
    @Column(name = "finaluser", length = 200)
    private String finalUser;

    /**
     * 交货时间
     */
    @Column(name = "DELIVERY_DATE", length = 50)
    private String deliveryDate;

    /**
     * 交货时间
     */
    @Column(name = "DELIVERY_DATE_STATUS")
    private Long deliveryDateStatus;

    /**
     * 设备付款方法
     */
    @Column(name = "PAYMENT_METHOD", length = 2000)
    private String paymentMethod;

    /**
     * 价格成交方式
     */
    @Column(name = "PRICE_TRANSACTION_WAY", length = 2000)
    private String price_transaction_way;

    /**
     * 交货地点
     */
    @Column(name = "PRICE_TERM", length = 2000)
    private String priceTerm;

    /**
     * 免费质量保证期
     */
    @Column(name = "DEFAULT_GUARANTEE", length = 50)
    private String default_guarantee;
    /**
     * 有偿延保
     */
    @Column(name = "PAID_EXTEND_WARRANTY", length = 50)
    private String paid_extend_warranty;


    /**
     * 外贸公司境外公司签订方
     */
    @Column(name = "FOREIGN_TRADE_COMPANY", length =200)
    private String foreign_trade_company;


    /**
     * 产品金额
     */
    @Column(name = "CARGOTOTAL")
    private Double cargoTotal;

    /**
     * 项目总金额
     */
    @Column(name = "AMOUNT", length =100)
    private String amount;

    /**
     * 项目总金额-人民币
     */
    @Column(name = "AMOUNTRMB",length = 100)
    private String amountRmb;

    /**
     * 币种
     */
    @Column(name = "CURRENCY", length = 20)
    private String currency;

    /**
     * 人民币汇率
     */
    @Column(name = "EXCHANGERATE", length = 32)
    private String exchangerate;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    /**
     * 数量
     */
    @Column(name = "QUANTITY", length = 100)
    private String quantity;

    /**
     * 包装要求
     */
    @Column(name = "PACKING_INSTRUCTION", length = 2000)
    private String packing_instruction;

    /**
     * 成交通知书
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "NOTICE_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment_n;

    /**
     * 加密的成交通知书
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "ENCRYPTNOTICE_ID", referencedColumnName = "ATTACH_ID")
    private Attachment encryptAttachment_n;

    /**
     * 合同
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "CONTRACT_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment_c;


    /**
     * 加密后的合同
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "ENCRYPTCONTRACT_ID", referencedColumnName = "ATTACH_ID")
    private Attachment encryptAttachment_c;

    /**
     * 采购结果通知书
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "PURCHASE_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment_p;


    /**
     * 可行性文件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JoinColumn(name = "FEASIBILITY_FILEID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment_feasibility;

    /**
     * 保修期
     */
    @Column(name = "GUARANTEE_DATE")
    private String guaranteeDate;

    /**
     * 维保费率/月
     */
    @Column(name = "GUARANTEE_FEE")
    private String guaranteeFee;


    /**
     * 创建人
     */
    @Column(name = "CREATOR", length = 100)
    private String creator;

    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    /**
     * 备注
     */
    @Column(name = "REMARK", length = 2000)
    private String remark;

//    /**
//     * 配件拓展表
//     */
//    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
//    @JoinColumn(name = "PART_ID")
//    private Set<PartInfoExp> partInfoExps = new HashSet<>();
//
//    /**
//     * 代理商拓展表
//     */
//    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
//    @JoinColumn(name = "AGENT_ID")
//    private Set<AgentInfoExp> agentInfoExps = new HashSet<>();
}
