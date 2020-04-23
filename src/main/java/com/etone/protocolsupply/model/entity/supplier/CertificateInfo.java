package com.etone.protocolsupply.model.entity.supplier;

import com.etone.protocolsupply.model.entity.Attachment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 三证信息
 */
@Data
@Entity
@Table(name = "CERTIFICATE_INFO")
public class CertificateInfo implements Serializable {

    @Id
    @Column(name = "CERTIFICATE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;

    /**
     * 是否三证合一
     */
    private Integer isCertificate;

    /**
     * 社会信用代码
     */
    private String creditCode;

    /**
     * 营业执照副本附件
     */
    @JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
    @OneToOne(fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "LICENSE", referencedColumnName = "ATTACH_ID")
    private Attachment license;

    /**
     * 法人身份证附件正面
     */
    @JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
    @OneToOne(fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "IDENTITYCARD_FRONT", referencedColumnName = "ATTACH_ID")
    private Attachment identityCardFront;

    /**
     * 法人身份证附件反面
     */
    @JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
    @OneToOne(fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "IDENTITYCARD_BACK", referencedColumnName = "ATTACH_ID")
    private Attachment identityCardBack;


    /**
     * 注册审核状态
     */
    @Column(name = "REGISTER_STATUS", length = 4)
    private Integer registerStatus;

    /**
     * 变更审核状态
     */
    @Column(name = "MODIFY_STATUS", length = 4)
    private Integer modifyStatus;

    //@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JoinColumn(name = "PARTNER_ID", referencedColumnName = "PARTNER_ID", nullable = false)
    //private PartnerInfo partnerInfo;
}
