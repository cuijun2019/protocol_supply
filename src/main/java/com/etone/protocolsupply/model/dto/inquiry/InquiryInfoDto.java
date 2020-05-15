package com.etone.protocolsupply.model.dto.inquiry;

import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import lombok.Data;

@Data
public class InquiryInfoDto extends InquiryInfo {
    private String id;
}
