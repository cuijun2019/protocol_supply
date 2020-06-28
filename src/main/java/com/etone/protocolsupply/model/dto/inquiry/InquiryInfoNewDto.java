package com.etone.protocolsupply.model.dto.inquiry;

import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import lombok.Data;

import java.util.List;

@Data
public class InquiryInfoNewDto extends InquiryInfoNew {
   private List<Long> inquiryIds;//询价ids
    private String actor;//当前登录人
   // private String id;
}
