package com.etone.protocolsupply.controller.agent;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.agent.AgentCollectionDto;
import com.etone.protocolsupply.model.dto.agent.AgentInfoDto;
import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.service.agent.AgentInfoService;
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
@RequestMapping(value = "${jwt.route.path}/agentInfo")
public class AgentInfoController extends GenericController {

    @Autowired
    private AgentInfoService agentInfoService;

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postAgent(@Validated
                                   @RequestBody AgentInfoDto agentInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        AgentInfo agentInfo = agentInfoService.save(agentInfoDto, this.getUser());
        responseBuilder.data(agentInfo);
        return responseBuilder.build();
    }

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
                                   HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<AgentInfo> specification = agentInfoService.getWhereClause(agentName, status, isDelete);
        Page<AgentInfo> page = agentInfoService.findAgents(specification, pageable);

        AgentCollectionDto agentCollectionDto = agentInfoService.to(page, request);
        responseBuilder.data(agentCollectionDto);

        return responseBuilder.build();
    }

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

    @ResponseBody
    @RequestMapping(value = "/{agentId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateAgent(@PathVariable("agentId") String agentId,
                                     @RequestBody AgentInfoDto agentInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        agentInfoDto.setAgentId(Long.parseLong(agentId));
        AgentInfo agentInfo = agentInfoService.update(agentInfoDto);
        responseBuilder.data(agentInfo);
        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{agentId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deleteAgent(@PathVariable("agentId") String agentId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        agentInfoService.delete(Long.parseLong(agentId));
        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportAgent(@RequestParam(value = "agentName", required = false) String agentName,
                            @RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "isDelete", required = false) String isDelete,
                            @RequestBody(required = false) List<Long> agentIds,
                            @Context HttpServletResponse response) {
        agentInfoService.export(response, agentName, status, isDelete, agentIds);
    }
}
