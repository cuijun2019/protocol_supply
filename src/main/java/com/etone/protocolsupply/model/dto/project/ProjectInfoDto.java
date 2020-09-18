package com.etone.protocolsupply.model.dto.project;

import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewDto;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ProjectInfoDto extends ProjectInfo {
    private String partnerId;//供应商id
    private String cargoId;
    private String cargoName;//货物名称
    private Double cargoTotal;//货物金额=配件*数量
    private String currency;//币种
    private String guaranteeRate;//维保率/月

    private String purchaseSchool;//采购人学院
    private String purchasePhone;//采购人联系方式

    /**
     * 关联询价id获取得到的信息
     */
    private String projectEntrustingUnit;//项目委托单位
    private String finalUser;//最终使用单位
    private String contact;//联系人
    private String contactPhone;//联系人电话
    private String fundsCardNumber;//经费卡号


    private String sfcgybw;//同一卡号，同一品目，同一项目的项目预算总数是否超过一百万



    /**
     * 配件拓展表
     */
    private Set<PartInfoExp> partInfoExps = new HashSet<>();

    /**
     * 代理商拓展表
     */
    //@JsonInclude(JsonInclude.Include.NON_NULL)
   // @JoinColumn(name = "CARGO_ID", referencedColumnName = "CARGO_ID")
    private AgentInfoExp agentInfoExp;

    private CargoInfo cargoInfo;

    private InquiryInfoNew inquiryInfo;

    private Set<BusiJbpmFlow> InquiryBusiJbpmFlows;
    private Set<BusiJbpmFlow> ProjectBusiJbpmFlows;

    private Set<InquiryInfoNewDto> inquiryInfoNewDto;

}
