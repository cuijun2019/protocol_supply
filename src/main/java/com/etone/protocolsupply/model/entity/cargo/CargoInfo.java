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
     * 最初货物id
     */
    @Column(name = "OLD_CARGOID", length = 20)
    private Long oldCargoId;

    /**
     * 货物序号
     */
    @Column(name = "CARGO_SERIAL", length = 100)
    private String cargoSerial;

    /**
     * 货物编号
     */
    @Column(name = "CARGO_Code", length = 100)
    private String cargoCode;

    /**
     * 货物品目Code
     */
    @Column(name = "ITEM_CODE", length = 100)
    private String itemCode;

    /**
     * 货物品目
     */
    @Column(name = "ITEM_NAME", length = 100)
    private String itemName;

    /**
     * 货物名称
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
     * 主要参数
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
     * 维保率/月
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
     * 参考价格
     */
    @Column(name = "REPRICE")
    private Double reprice;

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

//    /**
//     * 供应商
//     */
//    @OneToOne(fetch = FetchType.LAZY)
//    @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @JoinColumn(name = "PARTNER_ID", referencedColumnName = "PARTNER_ID")
//    private PartnerInfo partnerInfo;
    @Column(name = "PARTNER_ID", length = 20)
    private Long partnerId;

}
