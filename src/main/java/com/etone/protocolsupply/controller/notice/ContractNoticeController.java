package com.etone.protocolsupply.controller.notice;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.notice.ContractNoticeCollectionDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.notice.ContractNotice;
import com.etone.protocolsupply.model.entity.notice.ResultNotice;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.notice.ContractNoticeService;
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

@RestController
@RequestMapping(value = "${jwt.route.path}/contractNotice")
public class ContractNoticeController extends GenericController {

    @Autowired
    private ContractNoticeService ContractNoticeService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 生成合同通知书
     *
     * @param projectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{projectId}",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue postContractNotice(@Validated
                                          @PathVariable("projectId") String projectId) {
        //查询合同模板所在路径
        Attachment attachment = attachmentService.findContractTemplate();

        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ContractNotice contractNotice = ContractNoticeService.save(projectId, this.getUser(),attachment.getPath());
        responseBuilder.data(contractNotice);
        return responseBuilder.build();
    }

    /**
     * 合同列表
     *
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getContractList(@Validated
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "projectCode", required = false) String projectCode,
                                       @RequestParam(value = "projectSubject", required = false) String projectSubject,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "contractId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<ContractNotice> specification = ContractNoticeService.getWhereClause(projectCode, projectSubject);
        Page<ContractNotice> page = ContractNoticeService.findContractNotice(specification, pageable);
        ContractNoticeCollectionDto contractNoticeCollectionDto = ContractNoticeService.to(page, request);
        responseBuilder.data(contractNoticeCollectionDto);

        return responseBuilder.build();
    }

    /**
     * 处理合同信息（修改状态）
     *
     * @param contractNoticeId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{contractNoticeId}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateContractNotice(@PathVariable("contractNoticeId") String contractNoticeId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        ContractNotice contractNotice = ContractNoticeService.update(contractNoticeId);
        responseBuilder.data(contractNotice);

        return responseBuilder.build();
    }


    /**
     * 导出
     * @param contractNoticeIds
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportContract(@RequestBody(required = false) List<Long> contractNoticeIds,
                               @Context HttpServletResponse response) {
        ContractNoticeService.export(response, contractNoticeIds);
    }



    /**
     * 查看合同详情
     *
     * @param contractNoticeId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{contractNoticeId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getContractNoticeById(@Validated
                                          @PathVariable("contractNoticeId") String contractNoticeId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ContractNotice contractNotice = ContractNoticeService.getContractNoticeById(contractNoticeId);
        responseBuilder.data(contractNotice);
        return responseBuilder.build();
    }
}
