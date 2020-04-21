package com.etone.protocolsupply.model.dto.procedure;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class BusiApproveResultCollectionDto extends PagingDto<BusiApproveResultDto> {
    private List<BusiApproveResultDto> busiApproveResultDtos = new ArrayList();

    @Override
    public void add(BusiApproveResultDto item) {
        busiApproveResultDtos.add(item);
    }

    @Override
    public Iterator<BusiApproveResultDto> iterator() {
        return busiApproveResultDtos.iterator();
    }
}
