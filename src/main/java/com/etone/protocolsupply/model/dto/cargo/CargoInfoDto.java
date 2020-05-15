package com.etone.protocolsupply.model.dto.cargo;

import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import lombok.Data;

@Data
public class CargoInfoDto extends CargoInfo {
    private PartnerInfo partnerInfo;

    private Long cargoId;
    private String cargoName;
}
