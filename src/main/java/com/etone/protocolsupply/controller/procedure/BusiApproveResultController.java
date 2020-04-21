package com.etone.protocolsupply.controller.procedure;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.procedure.BusiApproveResultCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiApproveResultDto;
import com.etone.protocolsupply.model.entity.procedure.BusiApproveResult;
import com.etone.protocolsupply.service.procedure.BusiApproveResultService;
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
    private BusiApproveResultService busiApproveResultService;


    /**
     * 新增已办
     *
     * @param busiApproveResultDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postBusiApproveResult(@Validated
                                       @RequestBody BusiApproveResultDto busiApproveResultDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BusiApproveResult busiApproveResult = busiApproveResultService.save(busiApproveResultDto, this.getUser());
        responseBuilder.data(busiApproveResult);
        return responseBuilder.build();
    }

    /**
     * 已办列表
     * @param currentPage
     * @param pageSize
     * @param approveType
     * @param approveSubject
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
                                       @RequestParam(value = "approveType", required = false) String approveType,
                                       @RequestParam(value = "approveSubject", required = false) String approveSubject,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "approveStartTime");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<BusiApproveResult> specification = busiApproveResultService.getWhereClause(approveType, approveSubject);
        Page<BusiApproveResult> page = busiApproveResultService.findbusiApproveResults(specification, pageable);
        BusiApproveResultCollectionDto busiApproveResultCollectionDto = busiApproveResultService.to(page, request,this.getUser());
        responseBuilder.data(busiApproveResultCollectionDto);
        return responseBuilder.build();
    }

    /**
     * 导出已办
     * @param ids
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportAgent(@RequestBody(required = false) List<Long> ids,
                            @Context HttpServletResponse response) {
        busiApproveResultService.export(response, ids,this.getUser());
    }


}
