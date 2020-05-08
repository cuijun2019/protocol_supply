package com.etone.protocolsupply.model.dto.partner;

import com.etone.protocolsupply.model.entity.supplier.*;
import com.etone.protocolsupply.model.entity.user.User;
import lombok.Data;

import java.util.List;

@Data
public class PartnerInfoDto extends PartnerInfo {

    private List<ContactInfo> contactInfoList;

    private CertificateInfo certificateInfo;

    private BankInfo bankInfo;

    private String creditCode;

    private String realName;

    private String telephone;

    private String email;
}
