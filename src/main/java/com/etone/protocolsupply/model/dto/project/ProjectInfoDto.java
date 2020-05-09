package com.etone.protocolsupply.model.dto.project;

import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
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

    private InquiryInfo inquiryInfo;

}
