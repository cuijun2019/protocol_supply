package com.etone.protocolsupply.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "PART_INFO")
public class PartInfo implements Serializable {

    @Id
    @Column(name = "PART_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    partId;

    /**
     * 配件编号
     */
    @Column(name = "PART_CODE", length = 100)
    private String  partCode;

    /**
     * 配件名称
     */
    @Column(name = "PART_NAME", length = 200)
    private String  partName;

    /**
     * 型号/规格
     */
    @Column(name = "STANDARDS", length = 200)
    private String standards;

    /**
     * 产地/厂家
     */
    @Column(name = "MANUFACTOR", length = 200)
    private String manufactor;

    /**
     * 主要技术参数
     */
    @Column(name = "TECH_PARAMS", length = 2000)
    private String  techParams;

    /**
     * 单位
     */
    @Column(name = "UNIT", length = 100)
    private String  unit;

    /**
     * 数量
     */
    @Column(name = "QUANTITY", length = 100)
    private String  quantity;

    /**
     * 单价
     */
    @Column(name = "PRICE")
    private Double  price;

    /**
     * 总价
     */
    @Column(name = "TOTAL")
    private Double  total;

    /**
     * 备注
     */
    @Column(name = "REMARK", length = 2000)
    private String  remark;

    @Column(name = "IS_DELETE", length = 4)
    private Integer isDelete;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARGO_ID")
    private CargoInfo cargoInfo;
}
