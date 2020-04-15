package com.etone.protocolsupply.model.dto;

import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class PartExpCollectionDto extends PagingDto<PartInfoExpDto> {
    private List<PartInfoExpDto> partInfoExpDtos = new ArrayList();

    @Override
    public void add(PartInfoExpDto item) {
        partInfoExpDtos.add(item);
    }

    @Override
    public Iterator<PartInfoExpDto> iterator() {
        return partInfoExpDtos.iterator();
    }
}
