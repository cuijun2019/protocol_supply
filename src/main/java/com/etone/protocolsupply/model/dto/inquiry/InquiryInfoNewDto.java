package com.etone.protocolsupply.model.dto.inquiry;

import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class InquiryInfoNewDto extends InquiryInfoNew {
    private List<Long> inquiryIds;//询价ids
    private String actor;//当前登录人
    private String sffs;//是否发送询价记录

    private Set<BusiJbpmFlow> InquiryBusiJbpmFlows;
    private Set<BusiJbpmFlow> ProjectBusiJbpmFlows;
}
