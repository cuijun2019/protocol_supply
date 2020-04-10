package com.etone.protocolsupply.model.entity.procedure;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "BUSI_JBPM_FLOW")
public class BusiJbpmFlow {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BUSINESS_TYPE", length = 100)
    private String businessType; // 待办类型

    @Column(name = "BUSINESS_SUBJECT", length = 100)
    private String businessSubject; // 待办主题

    @Column(name = "TASKSTATE", length = 100)
    private String taskState; // 当前状态

    @Column(name = "PARENTACTOR", length = 100)
    private String parentActor; // 当前处理人

    @Column(name = "FLOWINITOR_ID", length = 100)
    private String flowInitorId; // 创建人ID

    @Column(name = "FLOW_STARTTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date flowStartTime; // 创建时间






}
