package com.etone.protocolsupply.model.entity.procedure;

import com.etone.protocolsupply.model.entity.Attachment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 待办
 */
@Data
@Entity
@Table(name = "BUSI_JBPM_FLOW")
public class BusiJbpmFlow implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 业务表id
     */
    @Column(name = "BUSINESS_ID", length = 100)
    private String businessId;

    /**
     * 审核流程
     */
    @Column(name = "FLOW_NODE_NAME", length = 100)
    private String flowNodeName;

    /**
     * 待办类型
     */
    @Column(name = "BUSINESS_TYPE", length = 100)
    private String businessType;

    /**
     * 询价单号
     */
    @Column(name = "INQUIRY_Code", length = 100)
    private String inquiryCode;

    /**
     * 待办主题（业务表名称）
     */
    @Column(name = "BUSINESS_SUBJECT", length = 100)
    private String businessSubject;

    /**
     * 当前状态
     */
    @Column(name = "TASK_STATE", length = 100)
    private String taskState;

    /**
     * 当前处理人
     */
    @Column(name = "PARENT_ACTOR", length = 100)
    private String parentActor;

    /**
     * 提交给的人员
     */
    @Column(name = "NEXT_ACTOR", length = 100)
    private String nextActor;

    /**
     * 创建人ID
     */
    @Column(name = "FLOW_INITOR_ID", length = 100)
    private String flowInitorId;

    /**
     * 创建时间
     */
    @Column(name = "FLOW_START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date flowStartTime;

    /**
     * 动作
     */
    @Column(name = "ACTION", length = 100)
    private String action;

    /**
     * 类型：0：待办 1：已办
     */
    @Column(name = "TYPE", length = 4)
    private Integer type;

    /**
     * 0：待阅  1：已阅
     */
    @Column(name = "READ_TYPE", length = 4)
    private Integer readType;

    /**
     * 审批意见
     */
    @Column(name = "OPINION", length = 2000)
    private String opinion;

    /**
     * 是否退回
     */
    @Column(name = "IS_BACK", length = 4)
    private Integer isBack;

    /**
     * 附件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;


}
