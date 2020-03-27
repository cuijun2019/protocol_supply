package com.etone.protocolsupply.model.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
@Data
@Entity
@Table(name = "BRAND_ITEM")
public class BrandItem implements Serializable {

    @Id
    @Column(name = "BRAND_ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    brandItemId;

    @Column(name = "PARENT_ITEM_CODE", length = 100)
    private String  parentItemCode;


    @Column(name = "ITEM_CODE", length = 100)
    private String  itemCode;


    @Column(name = "ITEM_NAME", length = 200)
    private String  itemName;

}
