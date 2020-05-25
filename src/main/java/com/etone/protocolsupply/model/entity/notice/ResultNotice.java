package com.etone.protocolsupply.model.entity.notice;

import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 采购结果通知书
 */
@Data
@Entity
@Table(name = "RESULT_NOTICE")
public class ResultNotice implements Serializable {

    @Id
    @Column(name = "RESULT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    /**
     * 项目编号
     */
    @Column(name = "PROJECT_CODE", length = 100)
    private String projectCode;

    /**
     * 项目主题
     */
    @Column(name = "PROJECT_SUBJECT", length = 1000)
    private String projectSubject;

    /**
     * 中标供应商
     */
    @Column(name = "SUPPLIER", length = 200)
    private String supplier;

    /**
     * 中标金额
     */
    @Column(name = "AMOUNT", length = 50)
    private String amount;

    /**
     * 状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    /**
     * 采购人
     */
    @Column(name = "PURCHASER", length = 100)
    private String purchaser;

    /**
     * 创建人（招标中心）
     */
    @Column(name = "CREATOR", length = 100)
    private String creator;

    /**
     * 采购结果通知书
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    /**
     * 签收时间
     */
    @Column(name = "SIGN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date signDate;


    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID",nullable = false)
    private ProjectInfo projectInfo;
}
