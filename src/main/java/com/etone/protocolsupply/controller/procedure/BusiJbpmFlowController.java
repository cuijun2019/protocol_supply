package com.etone.protocolsupply.controller.procedure;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.service.procedure.BusiJbpmFlowService;
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
@RequestMapping(value = "${jwt.route.path}/busiJbpmFlow")
public class BusiJbpmFlowController extends GenericController {

    @Autowired
    private BusiJbpmFlowService busiJbpmFlowService;
    @Autowired
    private ProjectInfoService projectInfoService;

    /**
     * 新增代办
     *
     * @param busiJbpmFlowDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postBusiJbpmFlow(@Validated
                                       @RequestBody BusiJbpmFlowDto busiJbpmFlowDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BusiJbpmFlow busiJbpmFlow = busiJbpmFlowService.save(busiJbpmFlowDto, this.getUser());
        responseBuilder.data(busiJbpmFlow);
        return responseBuilder.build();
    }

    /**
     * 代办列表
     * @param currentPage
     * @param pageSize
     * @param businessType
     * @param businessSubject
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getBusiJbpmFlows(@Validated
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "businessType", required = false) String businessType,
                                       @RequestParam(value = "businessSubject", required = false) String businessSubject,
                                       @RequestParam(value = "businessId", required = false) String businessId,
                                       @RequestParam(value = "type", required = false) Integer type,
                                       @RequestParam(value = "parentActor", required = false) String parentActor,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "flowStartTime");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<BusiJbpmFlow> specification = busiJbpmFlowService.getWhereClause(businessType, businessSubject, type,businessId,parentActor);
        Page<BusiJbpmFlow> page = busiJbpmFlowService.findAgents(specification, pageable);
        BusiJbpmFlowCollectionDto busiJbpmFlowDto = busiJbpmFlowService.to(page, request,this.getUser());
        responseBuilder.data(busiJbpmFlowDto);
        return responseBuilder.build();
    }

    /** 待办页面
     * 根据待办类型、状态，当前登录人查询我的项目
     * @param currentPage
     * @param pageSize
     * @param isDelete
     * @param businessType
     * @param parentActor
     * @param status
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/myProjectList"
            ,method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getCargoInfos(@Validated
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "isDelete", required = false) String isDelete,
                                       @RequestParam(value = "businessType", required = false) String businessType,
                                       @RequestParam(value = "parentActor", required = false) String parentActor,
                                       @RequestParam(value = "status", required = false) String status,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "projectId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<ProjectInfo> page = projectInfoService.findAllByBusiJbpmFlow(isDelete, businessType, parentActor,status, pageable);
        ProjectCollectionDto projectCollectionDto = projectInfoService.to(page, request);
        responseBuilder.data(projectCollectionDto);
        return responseBuilder.build();
    }

    /**
     * 导出待办

     * @param ids
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportAgent(@RequestParam(value = "type", required = false) Integer type,
                            @RequestParam(value = "parentActor", required = false) String parentActor,
                            @RequestBody(required = false) List<Long> ids,
                            @Context HttpServletResponse response) {
        busiJbpmFlowService.export(response,ids,type,parentActor);
    }


    /**
     * 根据业务表id，待办类型，当前处理人 修改type=1
     * @param busiJbpmFlowDto
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateBusiJbpmFlows(@Validated
                                             @RequestBody BusiJbpmFlowDto busiJbpmFlowDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Specification<BusiJbpmFlow> specification = busiJbpmFlowService.getWhereThreeClause(
                busiJbpmFlowDto.getBusinessId(),busiJbpmFlowDto.getBusinessType(),busiJbpmFlowDto.getParentActor(),busiJbpmFlowDto.getNextActor(),null);
        List<BusiJbpmFlow> list=busiJbpmFlowService.getModel(specification);
        BusiJbpmFlow busiJbpmFlow=new BusiJbpmFlow();
        if(list.size()!=0){
            busiJbpmFlow=list.get(0);
            busiJbpmFlowService.updateType(busiJbpmFlow.getId());
            busiJbpmFlow.setType(1);
        }else {
            responseBuilder.message("查询不到数据，操作失败！");
        }
        responseBuilder.data(busiJbpmFlow);
        return responseBuilder.build();
    }


    /**
     * 根据businessId、businessType、type、nextActor查询审核表是否存在
     * @param businessId
     * @param businessType
     * @param type
     * @param nextActor
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/isExist",method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue isExistBusiJbpmFlows(@Validated
                                              @RequestParam(value = "businessId", required = false) String businessId,
                                              @RequestParam(value = "businessType", required = false) String businessType,
                                              @RequestParam(value = "type", required = false) Integer type,
                                              @RequestParam(value = "parentActor", required = false) String parentActor,
                                              @RequestParam(value = "nextActor", required = false) String nextActor) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Specification<BusiJbpmFlow> specification = busiJbpmFlowService.getWhereThreeClause(businessId,businessType,parentActor,nextActor,type);
        List<BusiJbpmFlow> list=busiJbpmFlowService.getModel(specification);
        BusiJbpmFlow busiJbpmFlow=new BusiJbpmFlow();
        if(list.size()!=0){
            busiJbpmFlow=list.get(0);
        }
        responseBuilder.data(busiJbpmFlow);
        return responseBuilder.build();
    }

}
