package com.etone.protocolsupply.controller.cargo;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.service.AttachmentService;
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

@RestController
@RequestMapping(value = "${jwt.route.path}/partInfo")
public class PartInfoController extends GenericController {

    @Autowired
    private PartInfoService partInfoService;

    @Autowired
    private AttachmentService attachmentService;

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
        responseBuilder.data(partInfo);
        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getPartInfos(@Validated
                                      @RequestParam(value = "isDelete", required = false) String isDelete,
                                      @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                      @RequestParam(value = "cargoId", required = false) String cargoId,
                                      HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "part_id");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Page<PartInfo> page = partInfoService.findPartInfos(cargoId, isDelete, pageable);

        PartCollectionDto partCollectionDto = partInfoService.to(page, request);
        responseBuilder.data(partCollectionDto);

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/{partId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue deletePartInfo(@PathVariable("partId") String partId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        partInfoService.delete(Long.parseLong(partId));
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
                                    @RequestParam(value = "cargoId", required = false) String cargoId,
                                    @RequestParam(value = "projectId", required = false) String projectId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Attachment attachment = attachmentService.upload(uploadFile, this.getUser());
        partInfoService.upLoad(attachment, cargoId,projectId);
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseValue exportPart(@PathVariable("cargoId") String cargoId,
                                    @Context HttpServletResponse response) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        partInfoService.export(response, Long.parseLong(cargoId));

        return responseBuilder.build();
    }

    @ResponseBody
    @RequestMapping(value = "/downloadTemplate")
    public void downloadExcel(HttpServletResponse res) {
        FileInputStream inputStream = null;
        ServletOutputStream out = null;
        String fileName = "partInfoTemplate.xls";
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
}
