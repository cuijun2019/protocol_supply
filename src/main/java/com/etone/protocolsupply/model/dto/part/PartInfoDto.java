package com.etone.protocolsupply.model.dto.part;

import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import lombok.Data;

@Data
public class PartInfoDto extends PartInfo {
    private String cargoId;
    private String cargoName;
    private String projectId;
}
