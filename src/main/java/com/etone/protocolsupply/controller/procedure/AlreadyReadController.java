package com.etone.protocolsupply.controller.procedure;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.service.procedure.BusiJbpmFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${jwt.route.path}/alreadyRead")
public class AlreadyReadController extends GenericController {

    @Autowired
    private BusiJbpmFlowService busiJbpmFlowService;


    /**
     * 新增已阅
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
        BusiJbpmFlow busiJbpmFlow = busiJbpmFlowService.saveAlreadyRead(busiJbpmFlowDto, this.getUser());
        responseBuilder.data(busiJbpmFlow);
        return responseBuilder.build();
    }

    /**
     * 根据id修改readType=1（已阅）、待阅主题
     * @param busiJbpmFlowDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue updateBusiApproveResult(@Validated
                                               @RequestBody BusiJbpmFlowDto busiJbpmFlowDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BusiJbpmFlow busiJbpmFlow = busiJbpmFlowService.updateAlreadyRead(busiJbpmFlowDto);
        responseBuilder.data(busiJbpmFlow);
        return responseBuilder.build();
    }

}
