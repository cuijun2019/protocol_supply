package com.etone.protocolsupply.model.dto.partner;

import com.etone.protocolsupply.model.entity.supplier.*;
import lombok.Data;

import java.util.List;

@Data
public class PartnerInfoDto extends PartnerInfo {

    private List<ContactInfo> contactInfoList;

    private CertificateInfo certificateInfo;

    private BankInfo bankInfo;
}
