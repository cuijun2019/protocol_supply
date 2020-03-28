package com.etone.protocolsupply.controller;

import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.entity.BrandItem;
import com.etone.protocolsupply.service.BrandItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        List<BrandItem> list = brandItemService.getWhereClause(parentItemCode);
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
                                          @RequestParam(value = "itemName", required = false) String itemName) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<BrandItem> list = brandItemService.getWhereClauseTemp(itemName);
        responseBuilder.data(list);
        return responseBuilder.build();
    }


}
