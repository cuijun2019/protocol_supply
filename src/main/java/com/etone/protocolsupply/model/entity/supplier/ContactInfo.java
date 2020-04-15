package com.etone.protocolsupply.model.entity.supplier;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 联系人
 */
@Data
@Entity
@Table(name = "CONTACTER")
public class ContactInfo implements Serializable {

    @Id
    @Column(name = "CONTACT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    /**
     * 姓名
     */
    @Column(name = "FULLNAME", length = 32)
    private String fullname;

    /**
     * 身份证号码
     */
    @Column(name = "IDENTIFICATION", length = 32)
    private String identification;

    /**
     * 职务
     */
    @Column(name = "POST", length = 50)
    private String post;

    /**
     * 电话号码
     */
    @Column(name = "TELEPHONE", length = 20)
    private String telephone;

    /**
     * 电子邮箱
     */
    @Column(name = "EMAIL", length = 100)
    private String email;

    /**
     * 传真号
     */
    @Column(name = "FAX", length = 32)
    private String fax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "PARTNER_ID")
    private PartnerInfo partnerInfo;
}
