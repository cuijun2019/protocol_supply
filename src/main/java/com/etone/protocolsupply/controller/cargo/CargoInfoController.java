package com.etone.protocolsupply.controller.cargo;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.cargo.CargoCollectionDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.cargo.CargoInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.List;

@RestController
@RequestMapping(value = "${jwt.route.path}/cargoInfo")
public class CargoInfoController extends GenericController {

    @Autowired
    private CargoInfoService  cargoInfoService;
    @Autowired
    private AttachmentService attachmentService;

    /**
     * 新增产品
     *
     * @param cargoInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postCargoInfo(@Validated
                                       @RequestBody CargoInfoDto cargoInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        CargoInfo cargoInfo = cargoInfoService.save(cargoInfoDto, this.getUser());
        responseBuilder.data(cargoInfo);
        return responseBuilder.build();
    }

    /**
     * 产品列表
     *
     * @param isDelete
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getCargoInfos(@Validated
                                       @RequestParam(value = "isDelete", required = false) String isDelete,
                                       @RequestParam(value = "isUpdate", required = false,defaultValue = "2") String isUpdate,
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "cargoName", required = false) String cargoName,
                                       @RequestParam(value = "cName", required = false) String cName,
                                       @RequestParam(value = "cargoCode", required = false) String cargoCode,
                                       @RequestParam(value = "manufactor", required = false) String manufactor,
                                       @RequestParam(value = "actor", required = false) String actor,
                                       @RequestParam(value = "status", required = false) Integer status,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "maintenanceDate");//按照修改时间倒叙
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<CargoInfo> page = cargoInfoService.findCargoInfos(isDelete,isUpdate, cargoName,cName, cargoCode,this.getUser(),status, pageable);
        CargoCollectionDto cargoCollectionDto = cargoInfoService.to(page, request);
        for (CargoInfoDto cargoInfoDto : cargoCollectionDto.getCargoInfoDtos()) {
            cargoInfoDto.setPartInfos(null);
        }
        responseBuilder.data(cargoCollectionDto);

        return responseBuilder.build();
    }

    /**
     * 产品详情
     *
     * @param cargoId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{cargoId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getCargo(@PathVariable("cargoId") String cargoId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        CargoInfoDto cargoInfo = cargoInfoService.findOne(Long.parseLong(cargoId));
        //配件list
        cargoInfo.setPartInfos(null);
        responseBuilder.data(cargoInfo);
        return responseBuilder.build();
    }

    /**
     * 删除产品 (只能删除草稿状态)
     *
     * @param cargoIds
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue deleteCargo( @RequestBody(required = false) List<Long> cargoIds) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        cargoInfoService.delete(cargoIds);
        return responseBuilder.build();
    }

    /**
     * 产品编辑修改
     * @param cargoInfo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/edit",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue editCargo(@Validated @RequestBody CargoInfo cargoInfo) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        cargoInfo = cargoInfoService.edit(cargoInfo, this.getUser());
        responseBuilder.data(cargoInfo);
        return responseBuilder.build();
    }




    /**
     * 产品变更
     * @param cargoInfo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateCargo(@Validated @RequestBody CargoInfo cargoInfo) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        cargoInfo = cargoInfoService.update(cargoInfo, this.getUser());
        responseBuilder.data(cargoInfo);
        return responseBuilder.build();
    }

    /**
     * 变更历史列表
     * @param isUpdate
     * @param currentPage
     * @param pageSize
     * @param cargoName
     * @param cargoCode
     * @param oldCargoId
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateList",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getCargoInfoUpdateList(@Validated
                                       @RequestParam(value = "isUpdate", required = false,defaultValue = "1") String isUpdate,
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "cargoName", required = false) String cargoName,
                                       @RequestParam(value = "cargoCode", required = false) String cargoCode,
                                       @RequestParam(value = "oldCargoId", required = false) String oldCargoId,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<CargoInfo> page = cargoInfoService.findCargoInfoUpdateList(isUpdate, cargoName, cargoCode,oldCargoId, pageable);
        CargoCollectionDto cargoCollectionDto = cargoInfoService.to(page, request);
        for (CargoInfoDto cargoInfoDto : cargoCollectionDto.getCargoInfoDtos()) {
            cargoInfoDto.setPartInfos(null);
        }
        responseBuilder.data(cargoCollectionDto);

        return responseBuilder.build();
    }




    /**
     * 产品导出--不使用这个功能
     *
     * @param cargoInfoDto
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue exportPart(@RequestBody CargoInfoDto cargoInfoDto,
                                    @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        cargoInfoService.export(response, cargoInfoDto.getCargoIds(),cargoInfoDto.getActor());
        return responseBuilder.build();
    }

    /**
     * 下载产品导入模板--不使用这个功能
     *
     */
    @ResponseBody
    @RequestMapping(
            value = "/downloadTemplate",
            method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue download( @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        String attachName="cargoInfoTemplate.xls";
        cargoInfoService.downloadByName(response);
        return responseBuilder.build();
    }

    /**
     * 产品导入--不使用这个功能
     *
     * @param uploadFile
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/upLoad",
            method = RequestMethod.POST,
            produces = {"application/json"},
            consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue upLoadPart(@Validated @RequestParam("file") MultipartFile uploadFile
                                    ,@RequestParam(value = "partnerId", required = false) Long partnerId) {
            ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Attachment attachment = attachmentService.upload(uploadFile, this.getUser());
        cargoInfoService.upLoad(attachment, this.getUser(),partnerId);
        return responseBuilder.build();
    }
}
