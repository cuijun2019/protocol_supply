package com.etone.protocolsupply.controller.template;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.template.BidTemplateCollectionDto;
import com.etone.protocolsupply.model.dto.template.BidTemplateDto;
import com.etone.protocolsupply.model.entity.template.BidTemplate;
import com.etone.protocolsupply.service.template.BidTemplateService;
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

/**
 * @author cuijun
 */
@RestController
@RequestMapping(value = "${jwt.route.path}/bidTemplate")
public class BidTemplateController extends GenericController {

    @Autowired
    private BidTemplateService bidTemplateService;

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postBidTemplate(@Validated
                                         @RequestBody BidTemplateDto bidTemplateDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BidTemplate bidTemplate = bidTemplateService.save(bidTemplateDto, this.getUser());
        responseBuilder.data(bidTemplate);
        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getBidTemplates(@Validated
                                         @RequestParam(value = "subject", required = false) String subject,
                                         @RequestParam(value = "status", required = false) String status,
                                         @RequestParam(value = "isDelete", required = false) String isDelete,
                                         @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                         HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<BidTemplate> specification = bidTemplateService.getWhereClause(subject, status, isDelete);
        Page<BidTemplate> page = bidTemplateService.findBidTemplates(specification, pageable);

        BidTemplateCollectionDto bidTemplateCollectionDto = bidTemplateService.to(page, request);
        responseBuilder.data(bidTemplateCollectionDto);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{bidTemplateId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getBidTemplate(@PathVariable("bidTemplateId") String bidTemplateId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        BidTemplate bidTemplate = bidTemplateService.findOne(Long.parseLong(bidTemplateId));
        responseBuilder.data(bidTemplate);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{bidTemplateId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateBidTemplate(@PathVariable("bidTemplateId") String bidTemplateId,
                                           @RequestBody BidTemplateDto bidTemplateDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        bidTemplateDto.setBidTemplateId(Long.parseLong(bidTemplateId));
        BidTemplate bidTemplate = bidTemplateService.update(bidTemplateDto, this.getUser());
        responseBuilder.data(bidTemplate);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{bidTemplateId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deleteBidTemplate(@PathVariable("bidTemplateId") String bidTemplateId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        bidTemplateService.delete(Long.parseLong(bidTemplateId));
        return responseBuilder.build();
    }
}
