package com.etone.protocolsupply.model.entity.cargo;

import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@ToString
@Entity
@Table(name = "CARGO_INFO")
public class CargoInfo implements Serializable {

    @Id
    @Column(name = "CARGO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cargoId;

    /**
     * 最初产品id
     */
    @Column(name = "OLD_CARGOID", length = 20)
    private Long oldCargoId;

    /**
     * 产品序号
     */
    @Column(name = "CARGO_SERIAL", length = 100)
    private String cargoSerial;

    /**
     * 产品编号
     */
    @Column(name = "CARGO_Code", length = 100)
    private String cargoCode;

    /**
     * 产品品目Code
     */
    @Column(name = "ITEM_CODE", length = 100)
    private String itemCode;

    /**
     * 产品品目
     */
    @Column(name = "ITEM_NAME", length = 100)
    private String itemName;

    /**
     * 产品名称
     */
    @Column(name = "CARGO_NAME", length = 200)
    private String cargoName;

    /**
     * 品牌
     */
    @Column(name = "BRAND", length = 200)
    private String brand;

    /**
     * 型号
     */
    @Column(name = "MODEL", length = 100)
    private String model;

    /**
     * 总体描述及产品性能
     */
    @Column(name = "MAIN_PARAMS", length = 2000)
    private String mainParams;

    /**
     * 产地
     */
    @Column(name = "MANUFACTOR", length = 100)
    private String manufactor;

    /**
     * 进口/国产类别
     */
    @Column(name = "TYPE", length = 20)
    private String type;

    /**
     * 币种
     */
    @Column(name = "CURRENCY", length = 20)
    private String currency;

    /**
     * 延长质保费率 %/年
     */
    @Column(name = "GUARANTEE_RATE", length = 20)
    private String guaranteeRate;

    /**
     * 备注
     */
    @Column(name = "REMARK", length = 2000)
    private String remark;

    /**
     * 审核状态
     */
    @Column(name = "STATUS", length = 4)
    private Integer status;

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
     * 维护人员
     */
    @Column(name = "MAINTENANCE_MAN", length = 32)
    private String maintenanceMan;

    /**
     * 维护时间
     */
    @Column(name = "MAINTENANCE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date maintenanceDate;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    /**
     * 是否已变更
     */
    @Column(name = "IS_UPDATE", length = 4)
    private Integer isUpdate;

    /**
     * 参考价格
     */
    @Column(name = "REPRICE", length = 50)
    private String reprice;

    /**
     * 证明文件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JoinColumn(name = "PROVE_FILE_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment;

    @OneToMany(mappedBy = "cargoInfo",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<PartInfo> partInfos = new HashSet<>();

    /**
     * 制造商
     */
    @Column(name = "PARTNER_ID", length = 20)
    private Long partnerId;




    /**
     *  20210126新增字段
     */
    /**
     * 产品彩页文件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JoinColumn(name = "PROBROCHURE_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment_probrochure;

    /**
     * 产品图片文件
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
    @JoinColumn(name = "PROPICTURE_ID", referencedColumnName = "ATTACH_ID")
    private Attachment attachment_propicture;

    /**
     * 制造商名称
     */
    @Column(name = "PARTNER_NAME", length = 200)
    private String partner_name;

    /**
     * 制造商联系人
     */
    @Column(name = "PARTNER_CONTACT", length = 32)
    private String partner_contact;

    /**
     * 制造商联系人电话
     */
    @Column(name = "PARTNER_CONTACT_NUMBER", length = 50)
    private String partner_contact_number;

    /**
     * 产品联系人
     */
    @Column(name = "PRODUCT_CONTACT", length = 32)
    private String product_contact;

    /**
     * 产品联系人电话
     */
    @Column(name = "PRODUCT_CONTACT_NUMBER", length = 50)
    private String product_contact_number;

    /**
     * 原厂默认质保期
     */
    @Column(name = "DEFAULT_GUARANTEE", length = 20)
    private String default_guarantee;

}
