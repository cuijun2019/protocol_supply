package com.etone.protocolsupply.model.dto;

import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import lombok.Data;

@Data
public class PartInfoExpDto extends PartInfoExp {
    private String cargoId;//产品id
    private String cargoName;//产品名称
    private Double cargoTotal;//产品金额=配件*数量
    private String currency;//币种
    private String guaranteeRate;//维保率/月

}
