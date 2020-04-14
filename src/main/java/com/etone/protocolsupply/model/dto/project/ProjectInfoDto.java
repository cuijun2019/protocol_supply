package com.etone.protocolsupply.model.dto.project;

import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import lombok.Data;

@Data
public class ProjectInfoDto extends ProjectInfo {
    private String partnerId;//供应商id
    private String cargoId;
    private String cargoName;

}
