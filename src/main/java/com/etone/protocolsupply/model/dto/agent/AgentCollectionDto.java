package com.etone.protocolsupply.model.dto.agent;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class AgentCollectionDto extends PagingDto<AgentInfoDto> {

    private List<AgentInfoDto> agentInfoDtos = new ArrayList();

    @Override
    public void add(AgentInfoDto item) {
        agentInfoDtos.add(item);
    }

    @Override
    public Iterator<AgentInfoDto> iterator() {
        return agentInfoDtos.iterator();
    }
}
