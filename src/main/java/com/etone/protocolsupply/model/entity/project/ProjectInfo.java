package com.etone.protocolsupply.model.entity.project;

import com.etone.protocolsupply.model.entity.AgentInfoExp;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.PartInfoExp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "PROJECT_INFO")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class ProjectInfo implements Serializable {

    @Id
    @Column(name = "PROJECT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    /**
     * 创建人
     */
    @Column(name = "CREATOR", length = 100)
    private String creator;

    /**
     * 采购人
     */
    @Column(name = "PURCHASER", length = 100)
    private String purchaser;

    /**
     * 项目编号
     */
    @Column(name = "PROJECT_CODE", length = 100)
    private String projectCode;

    /**
     * 项目主题
     */
    @Column(name = "PROJECT_SUBJECT", length = 1000)
    private String projectSubject;

    /**
     * 交货时间
     */
    @Column(name = "DELIVERY_DATE", length = 50)
    private Double deliveryDate;

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
     * 价格条款
     */
    @Column(name = "PRICE_TERM", length = 2000)
    private String priceTerm;

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
     * 审核状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    /**
     * 项目总金额
     */
    @Column(name = "AMOUNT", length = 50)
    private String amount;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    /**
     * 中标通知书
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "NOTICE_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment_n;

    /**
     * 合同
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "CONTRACT_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment_c;

    /**
     * 配件拓展表
     */
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "PART_ID")
    private Set<PartInfoExp> partInfoExps = new HashSet<>();

    /**
     * 代理商拓展表
     */
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "AGENT_ID")
    private Set<AgentInfoExp> agentInfoExps = new HashSet<>();
}
