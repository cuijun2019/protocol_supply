package com.etone.protocolsupply.model.entity.flowEngine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 流程线
 */
@Data
@Entity
@Table(name = "FLOW_TRANSFER")
public class FlowTransfer implements Serializable {

    /**
     * 流程线id
     */
    @Id
    @Column(name = "FLOW_TRANSFER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flowTransferId;

    /**
     * 上一节点编号
     */
    @Column(name = "PREV_NODE_CODE", length = 100)
    private String prevNodeCode;

    /**
     * 后一节点编号
     */
    @Column(name = "NEXT_NODE_CODE", length = 100)
    private String nextNodeCode;

    /**
     * 角色
     */
    @Column(name = "ROLES", length = 500)
    private String roles;

    /**
     * SQL语句
     */
    @Lob
    @Column(name = "SQL_STR", columnDefinition="text")
    private String sqlStr;

    /**
     * 备注
     */
    @Column(name = "REMARK", length = 1000)
    private String remark;

    /**
     * 关联流程id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "FLOW_ID")
    private FlowInfo flowInfo;
}
