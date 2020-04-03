package com.etone.protocolsupply.model.dto.systemControl;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class UserCollectionDto extends PagingDto<UserDto> {

    private List<UserDto> userDtos = new ArrayList<>();
    @Override
    public void add(UserDto item) {
        userDtos.add(item);
    }

    @Override
    public Iterator<UserDto> iterator() {
        return userDtos.iterator();
    }
}
