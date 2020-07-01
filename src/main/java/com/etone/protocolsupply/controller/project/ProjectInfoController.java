package com.etone.protocolsupply.controller.project;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.PartExpCollectionDto;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.service.cargo.PartInfoService;
import com.etone.protocolsupply.service.inquiry.InquiryInfoNewService;
import com.etone.protocolsupply.service.inquiry.InquiryInfoService;
import com.etone.protocolsupply.service.project.ProjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "${jwt.route.path}/projectInfo")
public class ProjectInfoController extends GenericController {

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private PartInfoService  partInfoService;
    @Autowired
    private InquiryInfoNewService inquiryInfoNewService;

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
     * @param pageSize
     * @param currentPage
     * @param isDelete
     * @param projectSubject
     * @param projectCode
     * @param status
     * @param inquiryId
     * @param actor
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getMyProject(@Validated
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "isDelete", required = false) String isDelete,
                                       @RequestParam(value = "projectSubject", required = false) String projectSubject,
                                       @RequestParam(value = "projectCode", required = false) String projectCode,
                                       @RequestParam(value = "status", required = false) String status,
                                       @RequestParam(value = "inquiryId", required = false) String inquiryId,
                                       @RequestParam(value = "actor", required = false) String actor,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "projectId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<ProjectInfo> page = projectInfoService.findMyProjectInfos(isDelete, projectSubject, projectCode, status,inquiryId,actor,pageable);
        ProjectCollectionDto projectCollectionDto = projectInfoService.to(page, request);
        responseBuilder.data(projectCollectionDto);
        return responseBuilder.build();
    }



    /**
     * 项目-配件列表
     *
     * @param isDelete
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/partInfoExp",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getPartInfos(@Validated
                                      @RequestParam(value = "isDelete", required = false) String isDelete,
                                      @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                      @RequestParam(value = "projectId", required = false) String projectId,
                                      HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "partId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<PartInfoExp> page = partInfoService.findPartInfoExps(projectId, isDelete, pageable);
        PartExpCollectionDto partInfoExpDtos = partInfoService.toExp(page, request);
        responseBuilder.data(partInfoExpDtos);
        return responseBuilder.build();
    }

    /**
     * 新增项目-查询供应商下所有审核通过的货物信息
     *
     * @param actor
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSetCargoInfo/{actor}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getSetCargoInfo(@Validated
                                             @PathVariable("actor") String actor) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<CargoInfo> cargoInfoDtos = projectInfoService.getSetCargoInfo(actor);
        responseBuilder.data(cargoInfoDtos);
        return responseBuilder.build();
    }

    /**
     * 新增项目配件列表
     *
     * @param projectInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/partInfoExp",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postpartInfoExpInfo(@Validated
                                         @RequestBody ProjectInfoDto projectInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Set<PartInfoExp> partInfoExps = projectInfoService.savePartExp(projectInfoDto, this.getUser());
        responseBuilder.data(partInfoExps);
        return responseBuilder.build();
    }


    /**
     * 删除-配件列表
     *
     * @param partExpId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/partInfoExp/{partExpId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deletepartInfoExp(@PathVariable("partExpId") String partExpId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        partInfoService.deleteExp(partExpId);
        return responseBuilder.build();
    }

    /**
     * 项目-配件列表导出
     *
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/partInfoExp/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue exportPartExp(@RequestBody(required = false) List<Long> partIds,
                                    @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        partInfoService.exportExp(response,partIds);
        return responseBuilder.build();
    }

    /**
     * 项目详情
     *
     * @param projectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{projectId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getProject(@PathVariable("projectId") String projectId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ProjectInfoDto projectInfoDto = projectInfoService.findOne(Long.parseLong(projectId));
        Set<BusiJbpmFlow> projectBusiJbpmFlows= inquiryInfoNewService.getSetBusiJbpmFlowList(Long.parseLong(projectId),"projectAudit");
        projectInfoDto.setProjectBusiJbpmFlows(projectBusiJbpmFlows);
        if(projectInfoDto.getInquiryId()!=null){
            Set<BusiJbpmFlow> inquiryBusiJbpmFlows= inquiryInfoNewService.getSetBusiJbpmFlowList(projectInfoDto.getInquiryId(),"enquiryAudit");
            projectInfoDto.setInquiryBusiJbpmFlows(inquiryBusiJbpmFlows);
        }
        responseBuilder.data(projectInfoDto);
        return responseBuilder.build();
    }

    /**
     * 修改项目
     *
     * @param projectId
     * @param projectInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{projectId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateProject(@PathVariable("projectId") String projectId,
                                       @RequestBody ProjectInfoDto projectInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        projectInfoDto.setProjectId(Long.parseLong(projectId));
        ProjectInfo projectInfo = projectInfoService.update(projectInfoDto, this.getUser());
        responseBuilder.data(projectInfo);
        return responseBuilder.build();
    }

    /**
     * 删除项目
     *
     * @param projectIds
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue deleteAgent(@RequestBody(required = false) List<Long> projectIds) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        projectInfoService.delete(projectIds);
        return responseBuilder.build();
    }

    /**
     * 项目导出
     *
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportAgent(
                            @RequestBody(required = false) List<Long> projectIds,
                            @RequestParam(value = "actor", required = false) String actor,
                            @Context HttpServletResponse response) {
        projectInfoService.export(response, projectIds,actor);
    }
}
