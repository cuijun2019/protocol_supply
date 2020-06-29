package com.etone.protocolsupply.controller.inquiry;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNotesCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNotesDto;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNotes;
import com.etone.protocolsupply.service.inquiry.InquiryInfoNotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "${jwt.route.path}/inquiryInfoNotes")
public class InquiryInfoNotesController extends GenericController {

    @Autowired
    private InquiryInfoNotesService inquiryInfoNotesService;


    /**
     * 新增保存询价记录
     *
     * @param inquiryInfoNotesDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postInquiryInfoNew(@Validated
                                       @RequestBody InquiryInfoNotesDto inquiryInfoNotesDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryInfoNotes inquiryInfoNotes = inquiryInfoNotesService.save(inquiryInfoNotesDto, this.getUser());
        responseBuilder.data(inquiryInfoNotes);
        return responseBuilder.build();
    }

    /**
     * 查询询价list
     * @param currentPage
     * @param pageSize
     * @param isDelete

     * @param actor 登录人
     * @param status 状态
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getInquiryInfoNewList(@Validated
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "isDelete", required = false ) String isDelete,
                                       @RequestParam(value = "actor", required = false) String actor,
                                       @RequestParam(value = "status", required = false) Integer status,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");//按照创建时间倒序
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<InquiryInfoNotes> page = inquiryInfoNotesService.findInquiryInfoNotesList(isDelete,  actor,status,pageable);
        InquiryInfoNotesCollectionDto inquiryInfoNotesCollectionDto = inquiryInfoNotesService.to(page, request);
        responseBuilder.data(inquiryInfoNotesCollectionDto);
        return responseBuilder.build();
    }






}
