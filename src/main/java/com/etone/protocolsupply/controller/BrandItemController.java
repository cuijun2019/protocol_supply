package com.etone.protocolsupply.controller;

import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.BrandItem;
import com.etone.protocolsupply.model.entity.CargoInfo;
import com.etone.protocolsupply.model.entity.PartInfo;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.BrandItemService;
import com.etone.protocolsupply.service.PartInfoService;
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
import java.util.List;

@RestController
@RequestMapping(value = "${jwt.route.path}/brandItem")
public class BrandItemController extends GenericController {

    @Autowired
    private BrandItemService brandItemService;

    @ResponseBody
    @RequestMapping(value = "/{parentItemCode}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getBrandItem(@PathVariable("parentItemCode") String parentItemCode) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<BrandItem> list= brandItemService.getWhereClause(parentItemCode);
        responseBuilder.data(list);
        return responseBuilder.build();
    }


    @ResponseBody
    @RequestMapping(
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getBrandItemTemp(@Validated
                                              @RequestParam(value = "ItemName", required = false) String ItemName) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
            List<BrandItem> list= brandItemService.getWhereClauseTemp(ItemName);
            responseBuilder.data(list);
        return responseBuilder.build();
    }




}
