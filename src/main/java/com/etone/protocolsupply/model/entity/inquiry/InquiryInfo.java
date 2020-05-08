package com.etone.protocolsupply.model.entity.inquiry;

import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "INQUIRY_INFO")
public class InquiryInfo implements Serializable {

    @Id
    @Column(name = "INQUIRY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    /**
     * 询价单号
     */
    @Column(name = "INQUIRY_CODE", length = 100)
    private String inquiryCode;

    /**
     * 采购人
     */
    @Column(name = "PURCHASER", length = 200)
    private String purchaser;

    /**
     * 采购单位
     */
    @Column(name = "UNIT", length = 200)
    private String unit;

    /**
     * 货物基本信息
     */
    @Column(name = "CARGOBASEINFO", length = 2000)
    private String cargoBaseInfo;

    /**
     * 询价时间
     */
    @Column(name = "INQUIRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inquiryDate;

    /**
     * 参考价格
     */
    @Column(name = "REPRICE", length = 20)
    private Double rePrice;
    /**
     * 状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    /**
     * 备注
     */
    @Column(name = "REMARK", length = 200)
    private String remark;

    /**
     * 供应商信息
     */
    @Column(name = "PARTNERMESSAGE", length = 2000)
    private String partnerMessage;

    /**
     * 证明文件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;


    /**
     * 供应商
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "PARTNER_ID", referencedColumnName = "PARTNER_ID")
    private PartnerInfo partnerInfo;

    /**
     * 货物
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "CARGO_ID", referencedColumnName = "CARGO_ID")
    private CargoInfo cargoInfo;
}
