package com.etone.protocolsupply.model.dto.cargo;

import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import lombok.Data;

import java.util.List;

@Data
public class CargoInfoDto extends CargoInfo {
    private PartnerInfo partnerInfo;
    private Long cargoId;
    private String cargoName;
    private String fullName;//联系人
    private String telephone;//联系人方式

    private List<Long> cargoIds;
    private String actor;

    private Integer sfbyy;//是否被引用
}
