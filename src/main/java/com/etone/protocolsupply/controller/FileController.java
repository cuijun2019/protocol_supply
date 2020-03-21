package com.etone.protocolsupply.controller;

import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "${jwt.route.path}")
public class FileController extends GenericController {

    @Autowired
    private AttachmentService attachmentService;

    @ResponseBody
    @RequestMapping(
            value = "/upload",
            method = RequestMethod.POST,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue upload(@Validated @RequestParam("file") MultipartFile uploadFile) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Attachment attachment = attachmentService.upload(uploadFile, this.getUser());
        Attachment saved = attachmentService.save(attachment);
        responseBuilder.data(saved);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(
            value = "/download/{attachId}",
            method = RequestMethod.GET,
            produces = {"application/json"},
            consumes = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue download(HttpServletResponse response, @PathVariable("attachId") String attachId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        attachmentService.download(response, Long.parseLong(attachId));

        return responseBuilder.build();
    }
}
