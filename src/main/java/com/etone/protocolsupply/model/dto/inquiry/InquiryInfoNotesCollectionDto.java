package com.etone.protocolsupply.model.dto.inquiry;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class InquiryInfoNotesCollectionDto extends PagingDto<InquiryInfoNotesDto> {
    private List<InquiryInfoNotesDto> inquiryInfoNotesDtos = new ArrayList();

    @Override
    public void add(InquiryInfoNotesDto item) {
        inquiryInfoNotesDtos.add(item);
    }

    @Override
    public Iterator<InquiryInfoNotesDto> iterator() {
        return inquiryInfoNotesDtos.iterator();
    }
}
