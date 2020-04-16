package com.etone.protocolsupply.model.entity.flowEngine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 流程节点
 */
@Data
@Entity
@Table(name = "FLOW_NODE")
public class FlowNode implements Serializable {

    /**
     * 流程节点id
     */
    @Id
    @Column(name = "FLOW_NODE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flowNodeId;

    /**
     * 流程节点编号
     */
    @Column(name = "FLOW_NODE_CODE", length = 100)
    private String flowNodeCode;

    /**
     * 流程节点名称
     */
    @Column(name = "FLOW_NODE_NAME", length = 300)
    private String flowNodeName;

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
