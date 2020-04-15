package com.etone.protocolsupply.model.entity.procedure;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "BUSI_APPROVE_RESULT")
public class BusiApproveResult implements Serializable {

    @Id
    @Column(name = "APPROVE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approveId;

    @Column(name = "APPROVE_TYPE", length = 100)
    private String approveType; // 已办类型

    @Column(name = "APPROVE_SUBJECT", length = 100)
    private String approveSubject; // 已办主题

    @Column(name = "APPROVESTATE", length = 100)
    private String approveState; // 当前状态

    @Column(name = "PARENTACTOR", length = 100)
    private String parentActor; // 当前处理人

    @Column(name = "APPROVEINITOR_ID", length = 100)
    private String approveInitorId; // 创建人ID

    @Column(name = "APPROVE_STARTTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approveStartTime; // 创建时间
}
