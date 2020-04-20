package com.etone.protocolsupply.model.dto.procedure;

import com.etone.protocolsupply.model.dto.PagingDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class BusiJbpmFlowCollectionDto extends PagingDto<BusiJbpmFlowDto> {
    private List<BusiJbpmFlowDto> busiJbpmFlowDtos = new ArrayList();

    @Override
    public void add(BusiJbpmFlowDto item) {
        busiJbpmFlowDtos.add(item);
    }

    @Override
    public Iterator<BusiJbpmFlowDto> iterator() {
        return busiJbpmFlowDtos.iterator();
    }
}
