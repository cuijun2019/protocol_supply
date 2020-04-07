package com.etone.protocolsupply.model.dto.project;

import com.etone.protocolsupply.model.dto.PagingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class ProjectCollectionDto extends PagingDto<ProjectInfoDto> {
    private List<ProjectInfoDto> projectInfoDtos = new ArrayList();

    @Override
    public void add(ProjectInfoDto item) {
        projectInfoDtos.add(item);
    }

    @Override
    public Iterator<ProjectInfoDto> iterator() {
        return projectInfoDtos.iterator();
    }



}
