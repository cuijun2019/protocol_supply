package com.etone.protocolsupply.controller.agent;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.agent.AgentCollectionDto;
import com.etone.protocolsupply.model.dto.agent.AgentInfoDto;
import com.etone.protocolsupply.model.dto.partner.PartnerInfoDtoUsername;
import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.service.agent.AgentInfoService;
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
import java.util.Map;

@RestController
@RequestMapping(value = "${jwt.route.path}/agentInfo")
public class AgentInfoController extends GenericController {

    @Autowired
    private AgentInfoService agentInfoService;

    /**
     * 供应商代理商注册
     * @param registerData
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/register",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postAgent( @Validated
                                    @RequestBody Map<String,String> registerData) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        String save = agentInfoService.save(registerData);
        if(save.equals("该用户已经存在")){
            responseBuilder.message("该用户名已被注册");
            responseBuilder.code(233);
        }else {
            responseBuilder.message("注册成功");
        }
        return responseBuilder.build();
    }


    /**
     * 新建代理商
     * @param agentInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue saveAgent( @Validated
                                    @RequestBody AgentInfoDto agentInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        AgentInfo agentInfo = agentInfoService.saveAgent(agentInfoDto, this.getUser());
        responseBuilder.data(agentInfo);
        return responseBuilder.build();
    }

    /**
     * 分页查询代理商列表
     * @param agentName
     * @param status
     * @param isDelete
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getAgents(@Validated
                                   @RequestParam(value = "agentName", required = false) String agentName,
                                   @RequestParam(value = "status", required = false) String status,
                                   @RequestParam(value = "isDelete", required = false) String isDelete,
                                   @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                   @RequestParam(value = "actor", required = false) String actor,
                                   @RequestParam(value = "reviewStatus",required = false)String reviewStatus,
                                   HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<AgentInfo> page = agentInfoService.findAgentInfos(agentName, status, isDelete, actor,pageable,reviewStatus);
        AgentCollectionDto agentCollectionDto = agentInfoService.to(page, request);
        responseBuilder.data(agentCollectionDto);
        return responseBuilder.build();
    }


    /**
     * 查询符合条件的代理商，筛选条件为代理商名称
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAgentsList",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getAgentsList(@Validated
                                       @RequestParam(value = "agentName", required = false) String agentName) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        List<PartnerInfoDtoUsername> agentlist = agentInfoService.findAgentsList(agentName);
        responseBuilder.data(agentlist);
        return responseBuilder.build();
    }

    /**
     * 根据id查询代理商信息
     * @param agentId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{agentId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getAgent(@PathVariable("agentId") String agentId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        AgentInfo agentInfo = agentInfoService.findOne(Long.parseLong(agentId));
        responseBuilder.data(agentInfo);

        return responseBuilder.build();
    }

    /**
     * 更新代理商状态
     * @param agentInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateAgent",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue updateAgent(@Validated
                                     @RequestBody AgentInfoDto agentInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        AgentInfo agentInfo = agentInfoService.update(agentInfoDto);
        responseBuilder.data(agentInfo);
        responseBuilder.message("状态更新成功");
        return responseBuilder.build();
    }

    /**
     * 根据ids批量删除代理商
     * @param agentIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteAgents",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue deleteAgent(@RequestBody(required = false) List<Long> agentIds) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        agentInfoService.delete(agentIds);
        return responseBuilder.build();
    }

    /**
     * 代理商导出
     * @param agentName
     * @param status
     * @param isDelete
     * @param agentIds
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportAgent(@RequestParam(value = "agentName", required = false) String agentName,
                            @RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "isDelete", required = false) String isDelete,
                            @RequestBody(required = false) List<Long> agentIds,
                            @RequestParam(value = "actor", required = false) String actor,
                            @Context HttpServletResponse response) {
        agentInfoService.export(response, agentName, status, isDelete, agentIds,actor);
    }
}
