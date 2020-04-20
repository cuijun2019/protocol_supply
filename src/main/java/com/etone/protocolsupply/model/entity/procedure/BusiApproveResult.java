package com.etone.protocolsupply.model.entity.procedure;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 已办
 */
@Data
@Entity
@Table(name = "BUSI_APPROVE_RESULT")
public class BusiApproveResult implements Serializable {

    @Id
    @Column(name = "APPROVE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approveId;

    /**
     * 业务表id
     */
    @Column(name = "BUSINESS_ID", length = 100)
    private String businessId;

    /**
     * 流程节点编号
     */
    @Id
    @Column(name = "FLOW_NODE_CODE", length = 100)
    private String flowNodeCode;

    /**
     * 已办类型
     */
    @Column(name = "APPROVE_TYPE", length = 100)
    private String approveType;

    /**
     * 已办主题
     */
    @Column(name = "APPROVE_SUBJECT", length = 100)
    private String approveSubject;

    /**
     * 当前状态
     */
    @Column(name = "APPROVE_STATE", length = 100)
    private String approveState;

    /**
     * 当前处理人
     */
    @Column(name = "PARENT_ACTOR", length = 100)
    private String parentActor;

    /**
     * 创建人ID
     */
    @Column(name = "APPROVE_INITOR_ID", length = 100)
    private String approveInitorId;

    /**
     * 创建时间
     */
    @Column(name = "APPROVE_START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approveStartTime;
}
