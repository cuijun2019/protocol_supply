package com.etone.protocolsupply.controller.inquiry;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewDto;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;

import com.etone.protocolsupply.model.entity.user.Leaders;
import com.etone.protocolsupply.service.inquiry.InquiryInfoNewService;
import org.springframework.beans.BeanUtils;
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
@RequestMapping(value = "${jwt.route.path}/inquiryInfoNew")
public class InquiryInfoNewController extends GenericController {

    @Autowired
    private InquiryInfoNewService inquiryInfoNewService;



    /**
     * 采购人创建询价
     *
     * @param inquiryInfoNewDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postInquiryInfoNew(@Validated
                                       @RequestBody InquiryInfoNewDto inquiryInfoNewDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryInfoNew inquiryInfoNew = inquiryInfoNewService.save(inquiryInfoNewDto, this.getUser());
        responseBuilder.data(inquiryInfoNew);
        return responseBuilder.build();
    }

    /**
     * 新增项目时判断项目预算是否超过一百万
     */
    @ResponseBody
    @RequestMapping(value = "/getProjectInfoBudget",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getProjectInfoBudget(@Validated
                                              @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                              @RequestParam(value = "fundsCardNumber", required = false) String fundsCardNumber,
                                              @RequestParam(value = "itemName", required = false) String itemName,
                                              HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");//按照创建时间倒序
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<InquiryInfoNew> page = inquiryInfoNewService.getProjectInfoBudget(fundsCardNumber, itemName,pageable);
        InquiryInfoNewCollectionDto inquiryInfoNewCollectionDto = inquiryInfoNewService.getProjectInfoBudgetto(page,itemName, request);
        responseBuilder.data(inquiryInfoNewCollectionDto);
        return responseBuilder.build();
    }

    /**
     * 查询询价list
     * @param currentPage
     * @param pageSize
     * @param isDelete
     * @param inquiryCode 询价单号
     * @param cargoName 产品名称
     * @param actor 登录人
     * @param status 状态
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getInquiryInfoNewList(@Validated
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "isDelete", required = false ) String isDelete,
                                       @RequestParam(value = "inquiryCode", required = false) String inquiryCode,
                                       @RequestParam(value = "cargoName", required = false) String cargoName,
                                       @RequestParam(value = "actor", required = false) String actor,
                                       @RequestParam(value = "status", required = false) Integer status,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");//按照创建时间倒序
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<InquiryInfoNew> page = inquiryInfoNewService.findInquiryInfoNewList(isDelete, inquiryCode,cargoName, this.getUser(),status,pageable);
        InquiryInfoNewCollectionDto inquiryInfoNewCollectionDto = inquiryInfoNewService.to(page, request);
        responseBuilder.data(inquiryInfoNewCollectionDto);
        return responseBuilder.build();
    }

    /**
     * 询价详情
     *
     * @param inquiryId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{inquiryId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getInquiryInfoNew(@PathVariable("inquiryId") String inquiryId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryInfoNew inquiryInfoNew = inquiryInfoNewService.findOne(Long.parseLong(inquiryId));
        Set<BusiJbpmFlow> inquiryBusiJbpmFlows= inquiryInfoNewService.getSetBusiJbpmFlowList(Long.parseLong(inquiryId),"enquiryAudit");

        inquiryInfoNew.getCargoInfo().setPartInfos(null);
        InquiryInfoNewDto inquiryInfoNewDto=new InquiryInfoNewDto();
        if(inquiryInfoNew.getProjectId()!=null){
            Set<BusiJbpmFlow> projectBusiJbpmFlows = inquiryInfoNewService.getSetBusiJbpmFlowList(inquiryInfoNew.getProjectId(),"projectAudit");
            inquiryInfoNewDto.setProjectBusiJbpmFlows(projectBusiJbpmFlows);
        }
        inquiryInfoNewDto.setInquiryBusiJbpmFlows(inquiryBusiJbpmFlows);//询价节点
        BeanUtils.copyProperties(inquiryInfoNew, inquiryInfoNewDto);
        responseBuilder.data(inquiryInfoNewDto);
        return responseBuilder.build();
    }


    /**
     * 修改询价
     * @param inquiryInfoNewDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateInquiryInfoNew(@Validated
                                           @RequestBody InquiryInfoNewDto inquiryInfoNewDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryInfoNew inquiryInfoNew = inquiryInfoNewService.update(inquiryInfoNewDto);
        responseBuilder.data(inquiryInfoNew);
        return responseBuilder.build();
    }


    /**
     * 批量删除询价
     *
     * @param inquiryIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue deleteInquiry(@RequestBody(required = false) List<Long> inquiryIds) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        inquiryInfoNewService.delete(inquiryIds);
        return responseBuilder.build();
    }

    /**
     * 询价导出
     *
     * @param inquiryInfoNewDto
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue exportPart( @RequestBody InquiryInfoNewDto inquiryInfoNewDto,
                                     @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        inquiryInfoNewService.export(response, inquiryInfoNewDto.getInquiryIds(),inquiryInfoNewDto.getActor());
        return responseBuilder.build();
    }

    /**
     * 查询领导学院列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getGroupList",method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getGroupList() {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<Leaders> list = inquiryInfoNewService.getGroupList();
        responseBuilder.data(list);
        return responseBuilder.build();
    }


}
