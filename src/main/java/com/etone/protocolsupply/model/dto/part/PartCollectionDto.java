package com.etone.protocolsupply.model.dto.part;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class PartCollectionDto extends PagingDto<PartInfoDto> {
    private List<PartInfoDto> partInfoDtos = new ArrayList();

    @Override
    public void add(PartInfoDto item) {
        partInfoDtos.add(item);
    }

    @Override
    public Iterator<PartInfoDto> iterator() {
        return partInfoDtos.iterator();
    }
}
