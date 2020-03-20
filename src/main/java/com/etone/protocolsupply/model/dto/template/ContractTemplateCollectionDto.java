package com.etone.protocolsupply.model.dto.template;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class ContractTemplateCollectionDto extends PagingDto<ContractTemplateDto> {

    private List<ContractTemplateDto> contractTemplateDtos = new ArrayList();

    @Override
    public void add(ContractTemplateDto item) {
        contractTemplateDtos.add(item);
    }

    @Override
    public Iterator<ContractTemplateDto> iterator() {
        return contractTemplateDtos.iterator();
    }
}
