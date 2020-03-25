package com.etone.protocolsupply.controller.cargo;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.cargo.CargoCollectionDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.CargoInfo;
import com.etone.protocolsupply.model.entity.PartInfo;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.PartInfoService;
import com.etone.protocolsupply.service.cargo.CargoInfoService;
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
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

@RestController
@RequestMapping(value = "${jwt.route.path}/cargoInfo")
public class CargoInfoController extends GenericController {

    @Autowired
    private CargoInfoService cargoInfoService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 新增货物
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

        cargoInfoService.save(cargoInfoDto, this.getUser());
        responseBuilder.data(cargoInfoDto);
        return responseBuilder.build();
    }

    /**
     * 货物列表
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
                                      @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                      HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "cargoId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<CargoInfo> specification = cargoInfoService.getWhereClause(isDelete);
        Page<CargoInfo> page = cargoInfoService.findPartInfos(specification, pageable);

        CargoCollectionDto cargoCollectionDto = cargoInfoService.to(page, request);
        responseBuilder.data(cargoCollectionDto);

        return responseBuilder.build();
    }

    /**
     * 货物详情
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

        CargoInfo cargoInfo = cargoInfoService.findOne(Long.parseLong(cargoId));
        responseBuilder.data(cargoInfo);

        return responseBuilder.build();
    }

    /**
     * 删除货物
     * @param cargoId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete/{cargoId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deleteCargo(@PathVariable("cargoId") String cargoId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        cargoInfoService.delete(Long.parseLong(cargoId));
        return responseBuilder.build();
    }

    /**
     * 修改货物
     * @param cargoId
     * @param cargoInfoDto
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update/{cargoId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateCargo(@PathVariable("cargoId") String cargoId,
                                     @RequestBody CargoInfoDto cargoInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        cargoInfoDto.setCargoId(Long.parseLong(cargoId));
        CargoInfo cargoInfo = cargoInfoService.update(cargoInfoDto,this.getUser());
        responseBuilder.data(cargoInfo);

        return responseBuilder.build();
    }

    /**
     * 货物导出
     * @param cargoName
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue exportPart(@RequestParam(value = "cargoName", required = false) String cargoName,
                                    @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
         cargoName="名称";
        cargoInfoService.export(response, cargoName);

        return responseBuilder.build();
    }


}
