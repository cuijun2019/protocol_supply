package com.etone.protocolsupply.model.dto.systemControl;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class RoleCollectionDto extends PagingDto<RoleDto> {

    private List<RoleDto> userDtos = new ArrayList<>();

    @Override
    public void add(RoleDto item) {
        userDtos.add(item);
    }

    @Override
    public Iterator<RoleDto> iterator() {
        return userDtos.iterator();
    }
}
