package com.etone.protocolsupply.controller.inquiry;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.inquiry.InquiryNotesCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryNotesDto;
import com.etone.protocolsupply.model.entity.inquiry.InquiryNotes;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.inquiry.InquiryNotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "${jwt.route.path}/inquiryNotes")
public class InquiryNotesController extends GenericController {

    @Autowired
    private InquiryNotesService inquiryNotesService;


    /**
     * 创建询价记录
     *
     * @param inquiryNotesDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postInquiryInfoNew(@Validated
                                       @RequestBody InquiryNotesDto inquiryNotesDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryNotes inquiryNotes = inquiryNotesService.save(inquiryNotesDto, this.getUser());
        responseBuilder.data(inquiryNotes);
        return responseBuilder.build();
    }

    /**
     * 查询询价记录list
     * @param currentPage
     * @param pageSize
     * @param isDelete
     * @param inquiryId 询价id
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
                                       @RequestParam(value = "inquiryId", required = false) String inquiryId,
                                       @RequestParam(value = "status", required = false) Integer status,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.ASC, "createDate");//按照创建时间正序
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<InquiryNotes> page = inquiryNotesService.findInquiryNotesList(isDelete, inquiryId,status,pageable);
        InquiryNotesCollectionDto inquiryNotesCollectionDto = inquiryNotesService.to(page, request);
        responseBuilder.data(inquiryNotesCollectionDto);
        return responseBuilder.build();
    }

    /**
     * 批量删除询价记录
     *
     * @param notesIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue deleteInquiry(@RequestBody(required = false) List<Long> notesIds) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        inquiryNotesService.delete(notesIds);
        return responseBuilder.build();
    }



}
