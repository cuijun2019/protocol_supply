package com.etone.protocolsupply.controller.inquiry;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewDto;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.inquiry.InquiryInfoNewService;
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
@RequestMapping(value = "${jwt.route.path}/inquiryInfoNew")
public class InquiryInfoNewController extends GenericController {

    @Autowired
    private InquiryInfoNewService inquiryInfoNewService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 采购人创建询价
     *
     * @param inquiryInfoNewDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postInquiryInfoNew(@Validated
                                       @RequestBody InquiryInfoNewDto inquiryInfoNewDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryInfoNew inquiryInfoNew = inquiryInfoNewService.save(inquiryInfoNewDto, this.getUser());
        responseBuilder.data(inquiryInfoNew);
        return responseBuilder.build();
    }

    /**
     *
     * @param currentPage
     * @param pageSize
     * @param isDelete
     * @param inquiryTheme 询价主题
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
                                       @RequestParam(value = "inquiryTheme", required = false) String inquiryTheme,
                                       @RequestParam(value = "actor", required = false) String actor,
                                       @RequestParam(value = "status", required = false) Integer status,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");//按照创建时间倒序
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<InquiryInfoNew> page = inquiryInfoNewService.findInquiryInfoNewList(isDelete, inquiryTheme, actor,status,pageable);
        InquiryInfoNewCollectionDto inquiryInfoNewCollectionDto = inquiryInfoNewService.to(page, request);
        for (InquiryInfoNewDto inquiryInfoNewDto : inquiryInfoNewCollectionDto.getInquiryInfoNewDtos()) {
            inquiryInfoNewDto.getCargoInfo().setPartInfos(null);//必须要setnull，不然会error，Could not write JSON
        }
        responseBuilder.data(inquiryInfoNewCollectionDto);
        return responseBuilder.build();
    }
//
//    /**
//     * 询价详情
//     *
//     * @param inquiryId
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "/{inquiryId}",
//            method = RequestMethod.GET,
//            consumes = {"application/json"},
//            produces = {"application/json"})
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseValue getCargo(@PathVariable("inquiryId") String inquiryId) {
//        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
//        InquiryInfo inquiryInfo = inquiryInfoService.findOne(Long.parseLong(inquiryId));
//        if(null==inquiryInfo.getAttachment()){
//            Attachment attachment=new Attachment();
//            inquiryInfo.setAttachment(attachment);
//        }
//        inquiryInfo.getCargoInfo().setPartInfos(null);
//        if(inquiryInfo.getPartnerInfo()==null){
//            PartnerInfo partnerInfo=new PartnerInfo();
//            inquiryInfo.setPartnerInfo(partnerInfo);
//        }
//        responseBuilder.data(inquiryInfo);
//        return responseBuilder.build();
//    }
//
//
//    /**
//     * 删除询价
//     *
//     * @param inquiryIds
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "/delete",
//            method = RequestMethod.PUT,
//            consumes = {"application/json"},
//            produces = {"application/json"})
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseValue deleteInquiry(@RequestBody(required = false) List<Long> inquiryIds) {
//        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
//        inquiryInfoService.delete(inquiryIds);
//        return responseBuilder.build();
//    }






}
