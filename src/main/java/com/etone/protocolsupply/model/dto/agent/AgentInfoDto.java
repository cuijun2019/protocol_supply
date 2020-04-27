package com.etone.protocolsupply.model.dto.agent;

import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import lombok.Data;

@Data
public class AgentInfoDto extends AgentInfo {
    private PartnerInfo partnerInfo;
}
