package com.etone.protocolsupply.controller.template;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.template.ResultTemplateCollectionDto;
import com.etone.protocolsupply.model.dto.template.ResultTemplateDto;
import com.etone.protocolsupply.model.entity.template.ResultTemplate;
import com.etone.protocolsupply.service.template.ResultTemplateService;
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
@RequestMapping(value = "${jwt.route.path}/resultTemplate")
public class ResultTemplateController extends GenericController {

    @Autowired
    private ResultTemplateService resultTemplateService;

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postResultTemplate(@Validated
                                            @RequestBody ResultTemplateDto resultTemplateDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ResultTemplate resultTemplate = resultTemplateService.save(resultTemplateDto, this.getUser());
        responseBuilder.data(resultTemplate);
        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getResultTemplates(@Validated
                                            @RequestParam(value = "subject", required = false) String subject,
                                            @RequestParam(value = "status", required = false) String status,
                                            @RequestParam(value = "isDelete", required = false) String isDelete,
                                            @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                            HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<ResultTemplate> specification = resultTemplateService.getWhereClause(subject, status, isDelete);
        Page<ResultTemplate> page = resultTemplateService.findResultTemplates(specification, pageable);

        ResultTemplateCollectionDto resultTemplateCollectionDto = resultTemplateService.to(page, request);
        responseBuilder.data(resultTemplateCollectionDto);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{resultTemplateId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getResultTemplate(@PathVariable("resultTemplateId") String resultTemplateId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        ResultTemplate resultTemplate = resultTemplateService.findOne(Long.parseLong(resultTemplateId));
        responseBuilder.data(resultTemplate);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{resultTemplateId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateResultTemplate(@PathVariable("resultTemplateId") String resultTemplateId,
                                              @RequestBody ResultTemplateDto resultTemplateDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        resultTemplateDto.setResultTemplateId(Long.parseLong(resultTemplateId));
        ResultTemplate resultTemplate = resultTemplateService.update(resultTemplateDto, this.getUser());
        responseBuilder.data(resultTemplate);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{resultTemplateId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deleteResultTemplate(@PathVariable("resultTemplateId") String resultTemplateId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        resultTemplateService.delete(Long.parseLong(resultTemplateId));
        return responseBuilder.build();
    }
}
