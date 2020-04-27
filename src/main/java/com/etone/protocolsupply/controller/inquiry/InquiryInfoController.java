package com.etone.protocolsupply.controller.inquiry;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.inquiry.InquiryCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoDto;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.cargo.CargoInfoService;
import com.etone.protocolsupply.service.inquiry.InquiryInfoService;
import com.etone.protocolsupply.service.project.ProjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.List;

@RestController
@RequestMapping(value = "${jwt.route.path}/inquiryInfo")
public class InquiryInfoController extends GenericController {

    @Autowired
    private CargoInfoService  cargoInfoService;
    @Autowired
    private InquiryInfoService inquiryInfoService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private ProjectInfoService projectInfoService;

    /**
     * 新增询价
     *
     * @param inquiryInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postCargoInfo(@Validated
                                       @RequestBody InquiryInfoDto inquiryInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryInfo inquiryInfo = inquiryInfoService.save(inquiryInfoDto, this.getUser());
        responseBuilder.data(inquiryInfo);
        return responseBuilder.build();
    }

    /**
     * 询价列表
     * @param isDelete
     * @param currentPage
     * @param pageSize
     * @param cargoName
     * @param inquiryCode
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getCargoInfos(@Validated
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "isDelete", required = false) String isDelete,
                                       @RequestParam(value = "cargoName", required = false) String cargoName,
                                       @RequestParam(value = "inquiryCode", required = false) String inquiryCode,
                                       @RequestParam(value = "actor", required = false) String actor,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "inquiryId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<InquiryInfo> page = inquiryInfoService.findInquiryInfos(isDelete, cargoName, inquiryCode, actor,pageable);
        InquiryCollectionDto inquiryCollectionDto = inquiryInfoService.to(page, request);
        for (InquiryInfoDto inquiryInfoDto : inquiryCollectionDto.getInquiryInfoDtos()) {
            inquiryInfoDto.getCargoInfo().setPartInfos(null);
        }
        responseBuilder.data(inquiryCollectionDto);
        return responseBuilder.build();
    }

    /**
     * 询价详情
     *
     * @param inquiryId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{inquiryId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getCargo(@PathVariable("inquiryId") String inquiryId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryInfo inquiryInfo = inquiryInfoService.findOne(Long.parseLong(inquiryId));
        inquiryInfo.getCargoInfo().setPartInfos(null);
        responseBuilder.data(inquiryInfo);
        return responseBuilder.build();
    }

    /**
     * 删除询价
     *
     * @param inquiryId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{inquiryId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deleteCargo(@PathVariable("inquiryId") String inquiryId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        inquiryInfoService.delete(Long.parseLong(inquiryId));
        return responseBuilder.build();
    }

    /**
     * 修改询价status
     *
     * @param inquiryId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{inquiryId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateInquiryStatus(@Validated
                                             @PathVariable("inquiryId") String inquiryId,
                                             @RequestBody InquiryInfo inquiryInfo) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        InquiryInfo info=inquiryInfoService.updateStatus(Long.parseLong(inquiryId),inquiryInfo);
        info.getCargoInfo().setPartInfos(null);
        responseBuilder.data(info);
        return responseBuilder.build();
    }

    /**
     * 询价导出
     *
     * @param inquiryIds
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue exportPart( @RequestBody(required = false) List<Long> inquiryIds,
                                    @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        inquiryInfoService.export(response, inquiryIds);
        return responseBuilder.build();
    }



}
