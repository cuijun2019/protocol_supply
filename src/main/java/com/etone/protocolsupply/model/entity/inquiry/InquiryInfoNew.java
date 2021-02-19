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
     * 项目id
     */
    @Column(name = "PROJECT_ID", length = 20)
    private Long projectId;

    /**
     * 询价单号
     */
    @Column(name = "INQUIRY_CODE", length = 100)
    private String inquiryCode;


    /**
     * 项目预算
     */
    @Column(name = "PROJECT_BUDGET", length = 20)
    private Double projectBudget;

    /**
     * 预算编码
     */
    @Column(name = "BUDGET_CODING", length = 20)
    private String budget_coding;

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
     * 状态 1:草稿 2:审核中 5:完成询价  6：终止询价  7：建立项目
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

    /**
     * 询价进度条
     */
    @Column(name = "PROGRESSBAR", length = 4)
    private Integer progressBar;

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
     * 产品
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name = "CARGO_ID", referencedColumnName = "CARGO_ID")
    private CargoInfo cargoInfo;

    /**
     * 项目委托单位
     */
    @Column(name = "PROJECT_ENTRUSTING_UNIT", length = 200)
    private String projectEntrustingUnit;

    /**
     * 最终使用单位
     */
    @Column(name = "FINAL_USER", length = 200)
    private String finalUser;

    /**
     * 联系人
     */
    @Column(name = "CONTACT", length = 32)
    private String contact;

    /**
     * 联系人电话
     */
    @Column(name = "CONTACT_PHONE", length = 200)
    private String contactPhone;

    /**
     * 经办人
     */
    @Column(name = "OPERATOR", length = 32)
    private String operator;

    /**
     * 经办人联系电话
     */
    @Column(name = "OPERATOR_NUMBER", length = 200)
    private String operator_number;

    /**
     * 经费卡号
     */
    @Column(name = "FUNDS_CARD_NUMBER", length = 200)
    private String fundsCardNumber;
}
