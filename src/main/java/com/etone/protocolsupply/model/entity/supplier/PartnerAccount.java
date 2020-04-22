package com.etone.protocolsupply.model.entity.supplier;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "PARTNER_ACCOUNT")
public class PartnerAccount implements Serializable {

    /**
     * 供应商信息id（主键）
     */
    @Id
    @Column(name = "ACCOUNT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    /**
     * 账号
     */
    @Column(name = "ACCOUNT", length = 100)
    private String account;

    /**
     * 密码
     */
    @Column(name = "PASSWORD", length = 100)
    @NotNull
    @Size(min = 4, max = 100)
    private String password;

    /**
     * 手机
     */
    @Column(name = "TELEPHONE", length = 20)
    private String telephone;

    /**
     * 邮箱
     */
    @Column(name = "EMAIL", length = 100)
    private String email;

    /**
     * 传真
     */
    @Column(name = "FAX", length = 100)
    private String fax;

    //@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JoinColumn(name = "PARTNER_ID", referencedColumnName = "PARTNER_ID", nullable = false)
    //private PartnerInfo partnerInfo;
}
