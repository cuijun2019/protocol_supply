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

    private String itemName;//货物品目名称
    private double sum;//总预算
    private String sfcgybw;//同一卡号，同一品目，同一项目的项目预算总数是否超过一百万
}
