package com.etone.protocolsupply.model.dto.inquiry;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class InquiryInfoNewCollectionDto extends PagingDto<InquiryInfoNewDto> {
    private List<InquiryInfoNewDto> inquiryInfoNewDtos = new ArrayList();

    @Override
    public void add(InquiryInfoNewDto item) {
        inquiryInfoNewDtos.add(item);
    }

    @Override
    public Iterator<InquiryInfoNewDto> iterator() {
        return inquiryInfoNewDtos.iterator();
    }
}
