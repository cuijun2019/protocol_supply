package com.etone.protocolsupply.model.dto;

import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfoExp;
import lombok.Data;

@Data
public class PartInfoExpDto extends PartInfoExp {
    private String cargoId;//货物id
    private String cargoName;//货物名称
    private Double price;//货物金额=配件*数量
    private String currency;//币种
    private String guaranteeRate;//维保率/月

}
