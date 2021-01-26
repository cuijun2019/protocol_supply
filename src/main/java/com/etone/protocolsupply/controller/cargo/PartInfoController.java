package com.etone.protocolsupply.controller.cargo;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.BrandItem;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.cargo.CargoInfoService;
import com.etone.protocolsupply.service.cargo.PartInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping(value = "${jwt.route.path}/partInfo")
public class PartInfoController extends GenericController {


    @Autowired
    private PartInfoService partInfoService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 新增配件
     *
     * @param partInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postPartInfo(@Validated
                                      @RequestBody PartInfoDto partInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        PartInfo partInfo = partInfoService.save(partInfoDto);
        partInfo.setCargoInfo(null);
        responseBuilder.data(partInfo);
        return responseBuilder.build();
    }

    /**
     * 配件列表
     *
     * @param isDelete
     * @param currentPage
     * @param pageSize
     * @param cargoId
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getPartInfos(@Validated
                                      @RequestParam(value = "isDelete", required = false) String isDelete,
                                      @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                      @RequestParam(value = "cargoId", required = false) String cargoId,
                                      @RequestParam(value = "cargoName", required = false) String cargoName,
                                      @RequestParam(value = "cName", required = false) String cName,
                                      @RequestParam(value = "actor", required = false) String actor,
                                      HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<PartInfo> list = partInfoService.findPartInfosList(cargoId, isDelete,cargoName,cName,actor);
        PartCollectionDto partCollectionDto = partInfoService.toList(list, request);
        for (PartInfoDto partInfoDto : partCollectionDto.getPartInfoDtos()) {
            partInfoDto.getCargoInfo().setPartInfos(null);
            partInfoDto.setCargoInfo(null);
        }
        responseBuilder.data(partCollectionDto);
        return responseBuilder.build();
    }



    /**
     * 修改配件
     *
     * @param partId
     * @param partInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{partId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateAgent(@PathVariable("partId") String partId,
                                     @RequestBody PartInfoDto partInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        partInfoDto.setPartId(Long.parseLong(partId));
        PartInfo partInfo = partInfoService.update(partInfoDto);
        partInfo.setCargoInfo(null);
        responseBuilder.data(partInfo);
        return responseBuilder.build();
    }

    /**
     * 删除配件
     *
     * @param partIds
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue deletePartInfo( @RequestBody(required = false) List<Long> partIds) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        partInfoService.delete(partIds);
        return responseBuilder.build();
    }

    /**
     * 配件导入
     *
     * @param cargoId
     * @param uploadFile
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/upLoad",
            method = RequestMethod.POST,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue upLoadPart(@Validated @RequestParam("file") MultipartFile uploadFile,
                                    @RequestParam(value = "cargoId", required = false) String cargoId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Attachment attachment = attachmentService.upload(uploadFile, this.getUser());
        partInfoService.upLoad(attachment, cargoId);
        return responseBuilder.build();
    }

    /**
     * 配件导出
     *
     * @param cargoId
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export/{cargoId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportPart(@PathVariable("cargoId") String cargoId,
                                    @RequestBody(required = false) List<Long> partIds,
                                    @Context HttpServletResponse response) {
        partInfoService.export(response, cargoId,partIds);

    }

    /**
     * 下载配件导入模板
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(
            value = "/downloadTemplate",
            method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue download( @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        String attachName="partInfoTemplate.xls";
        partInfoService.downloadByName(response);
        return responseBuilder.build();
    }
}
