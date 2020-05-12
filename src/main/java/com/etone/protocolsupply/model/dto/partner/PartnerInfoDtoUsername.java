package com.etone.protocolsupply.model.dto.partner;

import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import lombok.Data;

@Data
public class PartnerInfoDtoUsername extends PartnerInfo {
    private String username;
}
