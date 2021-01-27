package com.etone.protocolsupply.model.dto.part;

import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import lombok.Data;

import java.util.List;

@Data
public class PartInfoDto extends PartInfo {
    private String cargoId;//产品id
    private String cargoName;//产品名称
    private Double price;//产品金额=配件*数量
    private String currency;//币种
    private String guaranteeRate;//维保率/月
    //private String projectId;

    private List<Long> partIds;


}
