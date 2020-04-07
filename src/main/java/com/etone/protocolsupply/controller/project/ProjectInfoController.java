package com.etone.protocolsupply.controller.project;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.cargo.CargoCollectionDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.cargo.CargoInfoService;
import com.etone.protocolsupply.service.cargo.PartInfoService;
import com.etone.protocolsupply.service.project.ProjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping(value = "${jwt.route.path}/projectInfo")
public class ProjectInfoController extends GenericController {

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private PartInfoService partInfoService;
    @Autowired
    private AttachmentService attachmentService;


    /**
     * 新增项目
     *
     * @param projectInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postProjectInfo(@Validated
                                       @RequestBody ProjectInfoDto projectInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ProjectInfo projectInfo = projectInfoService.save(projectInfoDto, this.getUser());

        responseBuilder.data(projectInfo);
        return responseBuilder.build();
    }

    /**
     * 项目列表
     * @param isDelete
     * @param currentPage
     * @param pageSize
     * @param projectSubject 项目主题
     * @param status 项目状态
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getCargoInfos(@Validated
                                       @RequestParam(value = "isDelete", required = false) String isDelete,
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "projectSubject", required = false) String projectSubject,
                                       @RequestParam(value = "status", required = false) String status,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "projectId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        Specification<ProjectInfo> specification = projectInfoService.getWhereClause(projectSubject, status, isDelete);
        Page<ProjectInfo> page = projectInfoService.findAgents(specification, pageable);

        ProjectCollectionDto projectCollectionDto = projectInfoService.to(page, request);
        responseBuilder.data(projectCollectionDto);

        return responseBuilder.build();
    }


}
