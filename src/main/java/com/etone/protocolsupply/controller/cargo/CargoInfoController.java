package com.etone.protocolsupply.controller.cargo;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.cargo.CargoCollectionDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
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
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping(value = "${jwt.route.path}/cargoInfo")
public class CargoInfoController extends GenericController {

    @Autowired
    private CargoInfoService cargoInfoService;

    @Autowired
    private PartInfoService partInfoService;
    @Autowired
    private AttachmentService attachmentService;


    /**
     * 新增货物
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
     * 货物列表
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
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "cargoName", required = false) String cargoName,
                                       @RequestParam(value = "partName", required = false) String partName,
                                       @RequestParam(value = "manufactor", required = false) String manufactor,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "cargo_id");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        Page<CargoInfo> page = cargoInfoService.findCargoInfos(isDelete, cargoName, partName, pageable);

        CargoCollectionDto cargoCollectionDto = cargoInfoService.to(page, request);
        responseBuilder.data(cargoCollectionDto);

        return responseBuilder.build();
    }

    /**
     * 货物详情
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

        CargoInfo cargoInfo = cargoInfoService.findOne(Long.parseLong(cargoId));

        //配件list
        //List<PartInfo> list=partInfoService.getPartInfoList(Long.parseLong(cargoId));
//        cargoInfo.setPartInfoList(list);
        responseBuilder.data(cargoInfo);

        return responseBuilder.build();
    }

    /**
     * 删除货物
     *
     * @param cargoId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{cargoId}",
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
     *
     * @param cargoId
     * @param cargoInfo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{cargoId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateCargo(@PathVariable("cargoId") String cargoId,
                                     @RequestBody CargoInfo cargoInfo) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        CargoInfo model = cargoInfoService.findOne(Long.parseLong(cargoId));
        cargoInfo.setCargoId(Long.parseLong(cargoId));
        cargoInfo.setCreator(model.getCreator());
        cargoInfo.setCreateDate(model.getCreateDate());
        cargoInfo.setCargoSerial(model.getCargoSerial());
        cargoInfo.setCargoCode(model.getCargoCode());
        cargoInfo = cargoInfoService.update(cargoInfo, this.getUser());
        responseBuilder.data(cargoInfo);
        return responseBuilder.build();
    }

    /**
     * 货物导出
     *
     * @param cargoIds
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue exportPart(@RequestParam(value = "cargoIds", required = false) String cargoIds,
                                    @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        cargoInfoService.export(response, cargoIds);
        return responseBuilder.build();
    }

    /**
     * 下载货物导入模板
     * @param res
     */
    @ResponseBody
    @RequestMapping(value = "/downloadTemplate")
    public void downloadExcel(HttpServletResponse res) {
        FileInputStream inputStream = null;
        ServletOutputStream out = null;
        String fileName = "cargoInfoTemplate.xls";
        try {
            res.setContentType("multipart/form-data");
            res.setCharacterEncoding("UTF-8");
            String filePath = getClass().getResource("/template/" + fileName).getPath();//文件在项目中的存放路径
            res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=utf-8''"
                    + URLEncoder.encode(fileName, "utf-8"));
            inputStream = new FileInputStream(filePath);
            out = res.getOutputStream();
            int b;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                // 4.写到输出流(out)中
                out.write(buffer, 0, b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 货物导入
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
    public ResponseValue upLoadPart(@Validated @RequestParam("file") MultipartFile uploadFile) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Attachment attachment = attachmentService.upload(uploadFile, this.getUser());
        cargoInfoService.upLoad(attachment,this.getUser());
        return responseBuilder.build();
    }


}
