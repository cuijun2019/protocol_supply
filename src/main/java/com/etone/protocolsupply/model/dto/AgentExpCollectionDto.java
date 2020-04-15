package com.etone.protocolsupply.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class AgentExpCollectionDto extends PagingDto<AgentInfoExpDto> {

    private List<AgentInfoExpDto> agentInfoDtoExps = new ArrayList();

    @Override
    public void add(AgentInfoExpDto item) {
        agentInfoDtoExps.add(item);
    }

    @Override
    public Iterator<AgentInfoExpDto> iterator() {
        return agentInfoDtoExps.iterator();
    }
}
