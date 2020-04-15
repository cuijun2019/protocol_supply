package com.etone.protocolsupply.controller.project;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.AgentExpCollectionDto;
import com.etone.protocolsupply.model.dto.PartExpCollectionDto;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.service.agent.AgentInfoService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.List;

@RestController
@RequestMapping(value = "${jwt.route.path}/projectInfo")
public class ProjectInfoController extends GenericController {

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private PartInfoService  partInfoService;
    @Autowired
    private AgentInfoService agentInfoService;


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
     *
     * @param isDelete
     * @param currentPage
     * @param pageSize
     * @param projectSubject 项目主题
     * @param status         项目状态
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
     * 项目-代理商列表
     *
     * @param projectId
     * @param isDelete
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/agentInfoExp",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getAgents(@Validated
                                   @RequestParam(value = "isDelete", required = false) String isDelete,
                                   @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                   @RequestParam(value = "projectId", required = false) String projectId,
                                   HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        Page<AgentInfoExp> page = agentInfoService.findAgentExps(projectId, isDelete, pageable);
        AgentExpCollectionDto agentCollectionDto = agentInfoService.toExp(page, request);
        responseBuilder.data(agentCollectionDto);
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
    public ResponseValue getCargo(@PathVariable("projectId") String projectId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ProjectInfo projectInfo = projectInfoService.findOne(Long.parseLong(projectId));
        //Long a=projectInfo.getCargoInfo().getCargoId();


        responseBuilder.data(projectInfo);
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
     * 删除
     *
     * @param projectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{projectId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deleteAgent(@PathVariable("projectId") String projectId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        projectInfoService.delete(Long.parseLong(projectId));
        return responseBuilder.build();
    }

    /**
     * 项目导出
     *
     * @param projectSubject
     * @param status
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportAgent(@RequestParam(value = "projectSubject", required = false) String projectSubject,
                            @RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "isDelete", required = false, defaultValue = "2") String isDelete,
                            @RequestBody(required = false) List<Long> projectIds,
                            @Context HttpServletResponse response) {
        projectInfoService.export(response, projectSubject, status, isDelete, projectIds);
    }
}
