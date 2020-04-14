package com.etone.protocolsupply.model.entity;

import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 代理商拓展表
 */
@Data
@Entity
@Table(name = "AGENT_INFO_EXP")
public class AgentInfoExp implements Serializable {

    @Id
    @Column(name = "AGENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agentId;

    /**
     * 代理商名称
     */
    @Column(name = "AGENT_NAME", length = 200)
    private String agentName;

    /**
     * 代理费扣点（百分比）
     */
    @Column(name = "AGENT_POINT", length = 50)
    private String agentPoint;

    /**
     * 状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    /**
     * 审核状态
     */
    @Column(name = "REVIEW_STATUS", length = 4)
    private Integer reviewStatus;

    @Column(name = "CREATOR", length = 32)
    private String creator;

    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    /**
     * 是否推荐供应商
     */
    @Column(name = "IS_RECOMMEND_SUPPLIER", length = 4)
    private Integer isRecommendSupplier;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    /**
     * 厂家授权函
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;

//    /**
//     * 项目
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @JoinColumn(name = "PROJECT_ID")
//    private ProjectInfo projectInfo;
//
//    /**
//     * 供应商
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @JoinColumn(name = "PARTNER_ID")
//    private PartnerInfo partnerInfo;
}
