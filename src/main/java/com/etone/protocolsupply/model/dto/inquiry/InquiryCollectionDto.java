package com.etone.protocolsupply.model.dto.inquiry;

import com.etone.protocolsupply.model.dto.PagingDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class InquiryCollectionDto extends PagingDto<InquiryInfoDto> {
    private List<InquiryInfoDto> inquiryInfoDtos = new ArrayList();

    @Override
    public void add(InquiryInfoDto item) {
        inquiryInfoDtos.add(item);
    }

    @Override
    public Iterator<InquiryInfoDto> iterator() {
        return inquiryInfoDtos.iterator();
    }
}
