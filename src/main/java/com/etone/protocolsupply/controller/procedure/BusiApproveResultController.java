package com.etone.protocolsupply.controller.procedure;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;

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
@RequestMapping(value = "${jwt.route.path}/busiApproveResult")
public class BusiApproveResultController extends GenericController {

    @Autowired
    private BusiJbpmFlowService busiJbpmFlowService;


    /**
     * 新增已办
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
    public ResponseValue postBusiApproveResult(@Validated
                                       @RequestBody BusiJbpmFlowDto busiJbpmFlowDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BusiJbpmFlow busiJbpmFlow = busiJbpmFlowService.saveBusiApproveResult(busiJbpmFlowDto, this.getUser());
        responseBuilder.data(busiJbpmFlow);
        return responseBuilder.build();
    }

    /**
     * 已办列表
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
                                          HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "flowStartTime");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<BusiJbpmFlow> specification = busiJbpmFlowService.getWhereClause(businessType, businessSubject, Constant.BUSINESS_TYPE_YIBAN,businessId);
        Page<BusiJbpmFlow> page = busiJbpmFlowService.findAgents(specification, pageable);
        BusiJbpmFlowCollectionDto busiJbpmFlowDto = busiJbpmFlowService.to(page, request,this.getUser());
        responseBuilder.data(busiJbpmFlowDto);
        return responseBuilder.build();
    }

    /**
     * 导出已办
     * @param businessType
     * @param businessSubject
     * @param ids
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportAgent(@RequestParam(value = "businessType", required = false) String businessType,
                            @RequestParam(value = "businessSubject", required = false) String businessSubject,
                            @RequestBody(required = false) List<Long> ids,
                            @Context HttpServletResponse response) {
        busiJbpmFlowService.export(response, businessType, businessSubject, ids,Constant.BUSINESS_TYPE_YIBAN);
    }

}
