package com.etone.protocolsupply.model.dto;

import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import lombok.Data;

@Data
public class AgentInfoExpDto extends AgentInfoExp {
    private String companyNo;//公司名称
}
