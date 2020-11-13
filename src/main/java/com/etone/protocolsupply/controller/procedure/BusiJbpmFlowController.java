package com.etone.protocolsupply.controller.procedure;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.entity.Attachment;
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
        if(busiJbpmFlow.getAttachment()==null){
            Attachment attachment=new Attachment();
            busiJbpmFlow.setAttachment(attachment);
        }
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
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize,
                                       @RequestParam(value = "businessType", required = false) String businessType,
                                       @RequestParam(value = "businessSubject", required = false) String businessSubject,
                                       @RequestParam(value = "businessId", required = false) String businessId,
                                       @RequestParam(value = "type", required = false) Integer type,
                                       @RequestParam(value = "readType", required = false) Integer readType,
                                       @RequestParam(value = "parentActor", required = false) String parentActor,
                                       @RequestParam(value = "nextActor", required = false) String nextActor,
                                       @RequestParam(value = "timeOrder", required = false, defaultValue = "DESC") String timeOrder,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort=null;
        sort = new Sort(Sort.Direction.ASC, "flowStartTime");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<BusiJbpmFlow> page = busiJbpmFlowService.findBusiJF(businessType, businessSubject,
                type,readType,businessId,parentActor,nextActor,timeOrder, pageable);
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
                                       @RequestParam(value = "isDelete", required = false, defaultValue = "2") String isDelete,
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
                            @RequestParam(value = "readType", required = false) Integer readType,
                            @RequestParam(value = "nextActor", required = false) String nextActor,
                            @RequestBody(required = false) List<Long> ids,
                            @Context HttpServletResponse response) {
        busiJbpmFlowService.export(response,ids,type,readType,nextActor);
    }


    /**
     * 根据业务表id，待办类型，提交给的人员 修改type=1
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
        List<BusiJbpmFlow> list = busiJbpmFlowService.getWhereThreeClause(
                busiJbpmFlowDto.getBusinessId(),busiJbpmFlowDto.getBusinessType(),busiJbpmFlowDto.getNextActor());
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
     * 根据业务表id，待办类型，type=0 修改nextActor,（需要上传可行性文件的就修改可行性文件id）
     * @param busiJbpmFlowDto
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateNextActor",method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateNextActor(@Validated
                                             @RequestBody BusiJbpmFlowDto busiJbpmFlowDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<BusiJbpmFlow> list = busiJbpmFlowService.updateNextActor(busiJbpmFlowDto.getBusinessId(),busiJbpmFlowDto.getBusinessType(),busiJbpmFlowDto.getType());
        BusiJbpmFlow busiJbpmFlow=new BusiJbpmFlow();
        if(list.size()!=0){
            busiJbpmFlow=list.get(0);
            busiJbpmFlowService.upnextActor(busiJbpmFlow.getId(),busiJbpmFlowDto.getNextActor(),busiJbpmFlowDto.getAttachment_feasibility());
            busiJbpmFlow.setNextActor(busiJbpmFlowDto.getNextActor());
            responseBuilder.data(busiJbpmFlow);
        }else {
            responseBuilder.message("查询不到数据，操作失败！");
            responseBuilder.data(null);
        }
        return responseBuilder.build();
    }


    /**
     * 判断当前登录人是否存在审核流程
     * @param businessId
     * @param businessType
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/isExistFlow",method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue isExistFlow(@Validated
                                              @RequestParam(value = "businessId", required = false) String businessId,
                                              @RequestParam(value = "businessType", required = false) String businessType,
                                              @RequestParam(value = "parentActor", required = false) String parentActor,
                                              @RequestParam(value = "nextActor", required = false) String nextActor) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<BusiJbpmFlow> list = busiJbpmFlowService.isBusiJbpmFlows(businessId,businessType,parentActor,nextActor);
        BusiJbpmFlow busiJbpmFlow=new BusiJbpmFlow();
        if(list.size()!=0){
            busiJbpmFlow=list.get(0);
            responseBuilder.data(busiJbpmFlow);
        }else {
            responseBuilder.data(null);
        }
        return responseBuilder.build();
    }

    /**
     * 根据businessId、businessType、type、nextActor判断是否存在数据
     * @param businessId
     * @param businessType
     * @param type
     * @param nextActor
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/isExistData",method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue isExistData(@Validated
                                              @RequestParam(value = "businessId", required = false) String businessId,
                                              @RequestParam(value = "businessType", required = false) String businessType,
                                              @RequestParam(value = "type", required = false) Integer type,
                                              @RequestParam(value = "parentActor", required = false) String parentActor,
                                              @RequestParam(value = "nextActor", required = false) String nextActor) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<BusiJbpmFlow> list = busiJbpmFlowService.isExistBusiJbpmFlows(businessId,businessType,parentActor,nextActor,type);
        BusiJbpmFlow busiJbpmFlow=new BusiJbpmFlow();
        if(list.size()!=0){
            busiJbpmFlow=list.get(0);
            responseBuilder.data(busiJbpmFlow);
        }else {
            responseBuilder.data(null);
        }
        return responseBuilder.build();
    }


//    @ResponseBody
//    @RequestMapping(value = "/returnTips",method = RequestMethod.GET,
//            consumes = {"application/json"},
//            produces = {"application/json"})
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseValue returnTips(@Validated
//                                     @RequestParam(value = "businessId", required = false) String businessId,
//                                     @RequestParam(value = "businessType", required = false) String businessType,
//                                     @RequestParam(value = "type", required = false) Integer type) {
//        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
//        List<BusiJbpmFlow> list = busiJbpmFlowService.isExistBusiJbpmFlows(businessId,businessType,parentActor,nextActor,type);
//        BusiJbpmFlow busiJbpmFlow=new BusiJbpmFlow();
//        if(list.size()!=0){
//            busiJbpmFlow=list.get(0);
//            responseBuilder.data(busiJbpmFlow);
//        }else {
//            responseBuilder.data(null);
//        }
//        return responseBuilder.build();
//    }



}
