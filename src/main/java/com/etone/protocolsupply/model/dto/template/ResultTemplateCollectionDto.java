package com.etone.protocolsupply.model.dto.template;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class ResultTemplateCollectionDto extends PagingDto<ResultTemplateDto> {

    private List<ResultTemplateDto> resultTemplateDtos = new ArrayList();

    @Override
    public void add(ResultTemplateDto item) {
        resultTemplateDtos.add(item);
    }

    @Override
    public Iterator<ResultTemplateDto> iterator() {
        return resultTemplateDtos.iterator();
    }
}
