package com.etone.protocolsupply.model.entity.flowEngine;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 流程类型
 */
@Data
@Entity
@Table(name = "FLOW_INFO")
public class FlowInfo implements Serializable {

    /**
     * 流程id
     */
    @Id
    @Column(name = "FLOW_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flowId;

    /**
     * 流程编号
     */
    @Column(name = "FLOW_CODE", length = 100)
    private String flowCode;

    /**
     * 流程名称
     */
    @Column(name = "FLOW_NAME", length = 300)
    private String flowName;

    /**
     * 流程JSON数据
     */
    @Lob
    @Column(name = "JSON_DATA", columnDefinition="text")
    private String jsonData;

    /**
     * 备注
     */
    @Column(name = "REMARK", length = 1000)
    private String remark;
}
