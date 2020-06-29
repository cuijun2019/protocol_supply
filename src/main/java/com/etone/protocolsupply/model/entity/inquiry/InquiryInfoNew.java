package com.etone.protocolsupply.model.entity.inquiry;

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
@Table(name = "INQUIRY_INFO_NEW")
public class InquiryInfoNew implements Serializable {

    @Id
    @Column(name = "INQUIRY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    /**
     * 询价单号
     */
    @Column(name = "INQUIRY_CODE", length = 100)
    private String inquiryCode;

//    /**
//     * 询价主题
//     */
//    @Column(name = "INQUIRY_THEME", length = 100)
//    private String inquiryTheme;

    /**
     * 项目预算
     */
    @Column(name = "PROJECT_BUDGET", length = 20)
    private Double projectBudget;

    /**
     * 项目背景
     */
    @Column(name = "PROJECT_BACKGROUND", length = 2000)
    private String projectBackground;

    /**
     * 采购人
     */
    @Column(name = "PURCHASER", length = 100)
    private String purchaser;

    /**
     * 产地/厂家
     */
    @Column(name = "MANUFACTOR", length = 200)
    private String manufactor;

    /**
     * 状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    /**
     * 是否删除
     */
    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    /**
     * 创建人
     */
    @Column(name = "CREATOR", length = 32)
    private String creator;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;


    /**
     * 附件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JoinColumn(name = "ATTACH_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;


    /**
     * 货物
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "CARGO_ID", referencedColumnName = "CARGO_ID")
    private CargoInfo cargoInfo;
}
