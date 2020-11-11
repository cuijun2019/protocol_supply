package com.etone.protocolsupply.controller.procedure;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.user.Leaders;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.cargo.CargoInfoService;
import com.etone.protocolsupply.service.inquiry.InquiryInfoService;
import com.etone.protocolsupply.service.procedure.BusiJbpmFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "${jwt.route.path}/toBeRead")
public class ToBeReadController extends GenericController {

    @Autowired
    private CargoInfoService  cargoInfoService;
    @Autowired
    private InquiryInfoService inquiryInfoService;
    @Autowired
    private BusiJbpmFlowService busiJbpmFlowService;
    @Autowired
    private AttachmentService attachmentService;

    /**
     * 新增待阅
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
    public ResponseValue postToBeRead(@Validated
                                       @RequestBody BusiJbpmFlowDto busiJbpmFlowDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BusiJbpmFlow busiJbpmFlow = busiJbpmFlowService.saveToBeRead(busiJbpmFlowDto, this.getUser());
        responseBuilder.data(busiJbpmFlow);
        return responseBuilder.build();
    }


}
