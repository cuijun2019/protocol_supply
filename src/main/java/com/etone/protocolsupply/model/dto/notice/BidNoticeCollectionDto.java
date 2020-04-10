package com.etone.protocolsupply.model.dto.notice;

import com.etone.protocolsupply.model.dto.PagingDto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BidNoticeCollectionDto extends PagingDto<BidNoticeDto> {
    private List<BidNoticeDto> bidNoticeDtos = new ArrayList<>();

    @Override
    public void add(BidNoticeDto item) {
        bidNoticeDtos.add(item);
    }

    @Override
    public Iterator<BidNoticeDto> iterator() {
        return bidNoticeDtos.iterator();
    }
}
