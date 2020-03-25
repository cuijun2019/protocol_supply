package com.etone.protocolsupply.model.dto.cargo;

import com.etone.protocolsupply.model.dto.PagingDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class CargoCollectionDto extends PagingDto<CargoInfoDto> {
    private List<CargoInfoDto> cargoInfoDtos = new ArrayList();

    @Override
    public void add(CargoInfoDto item) {
        cargoInfoDtos.add(item);
    }

    @Override
    public Iterator<CargoInfoDto> iterator() {
        return cargoInfoDtos.iterator();
    }
}
