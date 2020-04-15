package com.etone.protocolsupply.model.dto.notice;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class ContractNoticeCollectionDto extends PagingDto<ContractNoticceDto> {

    private List<ContractNoticceDto> contractNoticceDtos = new ArrayList<>();

    @Override
    public void add(ContractNoticceDto item) {
        contractNoticceDtos.add(item);
    }

    @Override
    public Iterator<ContractNoticceDto> iterator() {
        return contractNoticceDtos.iterator();
    }
}
