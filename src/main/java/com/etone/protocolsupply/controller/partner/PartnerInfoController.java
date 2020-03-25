package com.etone.protocolsupply.controller.partner;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.dto.partner.PartnerInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.PartInfo;
import com.etone.protocolsupply.model.entity.PartnerInfo;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.PartInfoService;
import com.etone.protocolsupply.service.partner.PartnerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

@RestController
@RequestMapping(value = "${jwt.route.path}/partnerInfo")
public class PartnerInfoController extends GenericController {

    @Autowired
    private PartnerInfoService partnerInfoService;

    @Autowired
    private AttachmentService attachmentService;

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postPartnerInfo(@Validated
                                      @RequestBody PartnerInfoDto partnerInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        partnerInfoService.save(partnerInfoDto);
        responseBuilder.data(partnerInfoDto);
        return responseBuilder.build();
    }








}
