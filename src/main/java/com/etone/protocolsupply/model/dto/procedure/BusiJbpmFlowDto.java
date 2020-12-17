package com.etone.protocolsupply.model.dto.procedure;

import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.user.Role;
import lombok.Data;

@Data
public class BusiJbpmFlowDto extends BusiJbpmFlow {
    private Long NextActor_roleId;//角色id
    /**
     * 角色名称
     * 将枚举的字符串写入
     */
    private String NextActor_roleName;
    private String NextActor_roleDescription;//角色描述
    private Integer NextActor_roleStatus;//状态

    private Attachment attachment_feasibility;//可行性论证文件

    private String companyName;//统一信用代码查询的公司名称



}
