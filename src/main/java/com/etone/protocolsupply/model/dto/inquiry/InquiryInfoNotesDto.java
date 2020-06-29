package com.etone.protocolsupply.model.dto.inquiry;

import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNotes;
import lombok.Data;

@Data
public class InquiryInfoNotesDto extends InquiryInfoNotes {
    private Long inquiryId;

}
