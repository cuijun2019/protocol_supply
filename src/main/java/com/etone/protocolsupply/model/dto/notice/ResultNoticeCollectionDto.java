package com.etone.protocolsupply.model.dto.notice;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class ResultNoticeCollectionDto extends PagingDto<ResultNoticeDto> {

    private List<ResultNoticeDto> resultNoticeDtos = new ArrayList<>();

    @Override
    public void add(ResultNoticeDto item) {
        resultNoticeDtos.add(item);
    }

    @Override
    public Iterator<ResultNoticeDto> iterator() {
        return resultNoticeDtos.iterator();
    }
}
