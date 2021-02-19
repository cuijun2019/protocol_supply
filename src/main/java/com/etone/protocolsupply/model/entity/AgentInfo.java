package com.etone.protocolsupply.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "AGENT_INFO")
public class AgentInfo implements Serializable {

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
     * 状态 激活1，不激活2
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    /**
     * 审核状态 审核通过1，审核未通过2
     */
    @Column(name = "REVIEW_STATUS", length = 4)
    private Integer reviewStatus;

    /**
     * 有效日期开始
     */
    @Column(name = "VALIDDATESTART")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validDateStart;

    /**
     * 有效日期结束
     */
    @Column(name = "VALIDDATEEND")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validDateEnd;

    @Column(name = "CREATOR", length = 32)
    private String creator;

    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

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

    /**
     * 关联的供应商id
     */
    @Column(name = "PARTNER_ID")
    private Long partnerId;
}
