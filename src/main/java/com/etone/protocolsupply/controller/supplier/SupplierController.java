package com.etone.protocolsupply.controller.supplier;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.partner.PartnerInfoCollectionDto;
import com.etone.protocolsupply.model.dto.partner.PartnerInfoDto;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.service.partner.PartnerInfoService;
import com.etone.protocolsupply.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "${jwt.route.path}/supplier")
public class SupplierController extends GenericController {

    @Autowired
    private PartnerInfoService partnerInfoService;

    @Autowired
    private UserService userService;

    /**
     * 我的信息
     * @param partnerId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{partnerId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getPartnerInfo(@PathVariable("partnerId") String partnerId) {

        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        PartnerInfoDto partnerInfoDto = partnerInfoService.findByPartnerId(Long.parseLong(partnerId));
        responseBuilder.data(partnerInfoDto);

        return responseBuilder.build();
    }


    /**
     * 我的变更
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(
            value = "/updatePartnerInfo",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updatePartnerInfo(@Validated
                                    @RequestBody PartnerInfoDto partnerInfoDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        boolean info = partnerInfoService.updatePartnerInfo(partnerInfoDto);
        if(info){
            responseBuilder.data(partnerInfoDto);
            responseBuilder.message("信息更新成功");
        }else {
            responseBuilder.message("信息更新失败");
        }
        return responseBuilder.build();
    }

    /**
     * 修改密码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updatePassword",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updatePassword(@RequestBody Map<String,String> jsonData) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Boolean result = userService.updatePassword(jsonData.get("oldPassword"), jsonData.get("newPassword"), this.getUser());
        if(result){
            responseBuilder.message("密码修改成功");
        }else {
            responseBuilder.code(404);
            responseBuilder.message("密码修改失败");
        }
        return responseBuilder.build();
    }


    /**
     * 分页查询供应商列表
     * @param supplierName  供应商名称
     * @param isDelete    是否删除 1删除   2未删除
     * @param Page 当前页码
     * @param pageSize    需要展示的条数
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getSuppliers(@Validated
                                  @RequestParam(value = "supplierName", required = false) String supplierName,
                                  @RequestParam(value = "isDelete", required = false) String isDelete,
                                  @RequestParam(value = "Page", required = false, defaultValue = "1") Integer Page,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                  HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "partnerId");
        Pageable pageable = PageRequest.of(Page - 1, pageSize, sort);
        Page<PartnerInfoDto> page = partnerInfoService.findPartnerInfoList(isDelete,supplierName, pageable);

        PartnerInfoCollectionDto partnerInfoCollectionDto = partnerInfoService.to(page, request);
        responseBuilder.data(partnerInfoCollectionDto);

        return responseBuilder.build();
    }

    /**
     * 导出
     *
     * @param supplierIds
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportSupplier(@RequestBody(required = false) List<Long> supplierIds,
                            @Context HttpServletResponse response) {
        partnerInfoService.export(response, supplierIds);
    }

}
