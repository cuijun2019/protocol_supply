package com.etone.protocolsupply.model.dto.partner;

import com.etone.protocolsupply.model.dto.PagingDto;
import com.etone.protocolsupply.model.dto.systemControl.RoleDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class PartnerInfoCollectionDto extends PagingDto<PartnerInfoDto> {

    private List<PartnerInfoDto> partnerInfoDtos = new ArrayList<>();

    @Override
    public void add(PartnerInfoDto item) {
        partnerInfoDtos.add(item);
    }

    @Override
    public Iterator<PartnerInfoDto> iterator() {
        return partnerInfoDtos.iterator();
    }
}
