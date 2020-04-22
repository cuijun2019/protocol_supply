package com.etone.protocolsupply.model.entity.supplier;

import com.etone.protocolsupply.model.entity.Attachment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "BANK_INFO")
public class BankInfo implements Serializable {

    @Id
    @Column(name = "BANK_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankId;

    /**
     * 开户银行
     */
    @Column(name = "DEPOSIT_BANK", length = 50)
    private String depositBank;

    /**
     * 银行基本账号
     */
    @Column(name = "BANK_ACCOUNT", length = 50)
    private String bankAccount;

    /**
     * 银行开户许可证附件
     */
    @JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
    @OneToOne(fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;


    //@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JoinColumn(name = "PARTNER_ID", referencedColumnName = "PARTNER_ID", nullable = false)
    //private PartnerInfo partnerInfo;
}
