package com.etone.protocolsupply.model.entity.supplier;

import com.etone.protocolsupply.model.entity.Attachment;
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
     * 银行开户许可证附件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "BANK_ATTACH", referencedColumnName = "ATTACH_ID")
    private Attachment bankAttach;

    /**
     * 营业执照副本附件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "LICENSE", referencedColumnName = "ATTACH_ID")
    private Attachment license;

    /**
     * 法人身份证附件
     */

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
}
