package com.etone.protocolsupply.model.dto.inquiry;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class InquiryNotesCollectionDto extends PagingDto<InquiryNotesDto> {
    private List<InquiryNotesDto> inquiryNotesDtos = new ArrayList();

    @Override
    public void add(InquiryNotesDto item) {
        inquiryNotesDtos.add(item);
    }

    @Override
    public Iterator<InquiryNotesDto> iterator() {
        return inquiryNotesDtos.iterator();
    }
}
