package com.etone.protocolsupply.model.dto.template;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class BidTemplateCollectionDto extends PagingDto<BidTemplateDto> {

    private List<BidTemplateDto> bidTemplateDtos = new ArrayList();

    @Override
    public void add(BidTemplateDto item) {
        bidTemplateDtos.add(item);
    }

    @Override
    public Iterator<BidTemplateDto> iterator() {
        return bidTemplateDtos.iterator();
    }
}
