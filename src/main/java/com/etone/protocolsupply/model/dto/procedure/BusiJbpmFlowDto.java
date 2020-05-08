package com.etone.protocolsupply.model.dto.procedure;

import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.user.Role;
import lombok.Data;

@Data
public class BusiJbpmFlowDto extends BusiJbpmFlow {
    private Role role;
}
