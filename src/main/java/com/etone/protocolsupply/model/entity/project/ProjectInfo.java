package com.etone.protocolsupply.model.entity.project;

import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "PROJECT_INFO")
public class ProjectInfo implements Serializable {

    @Id
    @Column(name = "PROJECT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    /**
     * 创建人
     */
    @Column(name = "CREATOR", length = 100)
    private String  creator;

    /**
     * 项目编号
     */
    @Column(name = "PROJECT_CODE", length = 100)
    private String  projectCode;

    /**
     * 项目主题
     */
    @Column(name = "PROJECT_SUBJECT", length = 1000)
    private String  projectSubject;

    /**
     * 交货时间
     */
    @Column(name = "DELIVERY_DATE", length = 50)
    private String deliveryDate;

    /**
     * 设备付款方法
     */
    @Column(name = "PAYMENT_METHOD", length = 4)
    private Integer paymentMethod;

    /**
     * 价格条款
     */
    @Column(name = "PRICE_TERM", length = 4)
    private Integer priceTerm;

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

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    @JsonIgnore
    @OneToMany(mappedBy = "projectInfo",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<PartInfo> partInfos = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "projectInfo",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<AgentInfo> agentInfos = new HashSet<>();
}
