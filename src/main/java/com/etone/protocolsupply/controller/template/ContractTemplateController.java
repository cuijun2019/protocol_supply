package com.etone.protocolsupply.controller.template;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.template.ContractTemplateCollectionDto;
import com.etone.protocolsupply.model.dto.template.ContractTemplateDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.ContractTemplate;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.service.template.ContractTemplateService;
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

/**
 * @author cuijun
 */
@RestController
@RequestMapping(value = "${jwt.route.path}/contractTemplate")
public class ContractTemplateController extends GenericController {

    @Autowired
    private ContractTemplateService contractTemplateService;
    @Autowired
    private AttachmentRepository    attachmentRepository;

    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postContractTemplate(@Validated
                                            @RequestBody ContractTemplateDto contractTemplateDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ContractTemplate contractTemplate = contractTemplateService.save(contractTemplateDto, this.getUser());
        responseBuilder.data(contractTemplate);
        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(
            value = "/upload",
            method = RequestMethod.POST,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue upload(@Validated @RequestParam("file") MultipartFile uploadFile) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Attachment attachment = contractTemplateService.upload(uploadFile, this.getUser());
        Attachment saved = attachmentRepository.save(attachment);
        responseBuilder.data(saved);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getContractTemplates(@Validated
                                            @RequestParam(value = "subject", required = false) String subject,
                                            @RequestParam(value = "status", required = false) String status,
                                            @RequestParam(value = "isDelete", required = false) String isDelete,
                                            @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                            HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<ContractTemplate> specification = contractTemplateService.getWhereClause(subject, status, isDelete);
        Page<ContractTemplate> page = contractTemplateService.findContractTemplates(specification, pageable);

        ContractTemplateCollectionDto contractTemplateCollectionDto = contractTemplateService.to(page, request);
        responseBuilder.data(contractTemplateCollectionDto);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{contractTemplateId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getContractTemplate(@PathVariable("contractTemplateId") String contractTemplateId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        ContractTemplate contractTemplate = contractTemplateService.findOne(Long.parseLong(contractTemplateId));
        responseBuilder.data(contractTemplate);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{contractTemplateId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateContractTemplate(@PathVariable("contractTemplateId") String contractTemplateId,
                                              @RequestBody ContractTemplateDto contractTemplateDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        contractTemplateDto.setContractTemplateId(Long.parseLong(contractTemplateId));
        ContractTemplate contractTemplate = contractTemplateService.update(contractTemplateDto, this.getUser());
        responseBuilder.data(contractTemplate);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{contractTemplateId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deleteContractTemplate(@PathVariable("contractTemplateId") String contractTemplateId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        contractTemplateService.delete(Long.parseLong(contractTemplateId));
        return responseBuilder.build();
    }
}
