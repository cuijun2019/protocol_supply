package com.etone.protocolsupply.model.entity.procedure;

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
     * 流程节点编号
     */
    @Id
    @Column(name = "FLOW_NODE_CODE", length = 100)
    private String flowNodeCode;

    /**
     * 待办类型
     */
    @Column(name = "BUSINESS_TYPE", length = 100)
    private String businessType;

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
}
