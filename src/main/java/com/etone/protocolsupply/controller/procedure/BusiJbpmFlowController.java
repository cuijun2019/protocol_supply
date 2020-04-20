package com.etone.protocolsupply.controller.procedure;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.agent.AgentCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.cargo.CargoInfoService;
import com.etone.protocolsupply.service.inquiry.InquiryInfoService;
import com.etone.protocolsupply.service.procedure.BusiJbpmFlowService;
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
    private CargoInfoService  cargoInfoService;
    @Autowired
    private InquiryInfoService inquiryInfoService;
    @Autowired
    private BusiJbpmFlowService busiJbpmFlowService;
    @Autowired
    private AttachmentService attachmentService;

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
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "flowStartTime");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<BusiJbpmFlow> specification = busiJbpmFlowService.getWhereClause(businessType, businessSubject);
        Page<BusiJbpmFlow> page = busiJbpmFlowService.findAgents(specification, pageable);
        BusiJbpmFlowCollectionDto busiJbpmFlowDto = busiJbpmFlowService.to(page, request,this.getUser());
        responseBuilder.data(busiJbpmFlowDto);
        return responseBuilder.build();
    }



}
