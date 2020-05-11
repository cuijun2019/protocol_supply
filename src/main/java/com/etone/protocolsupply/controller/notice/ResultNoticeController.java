package com.etone.protocolsupply.controller.notice;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.notice.ResultNoticeCollectionDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.notice.ResultNotice;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.notice.ResultNoticeService;
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
@RequestMapping(value = "${jwt.route.path}/resultNotice")
public class ResultNoticeController extends GenericController {

    @Autowired
    private ResultNoticeService resultNoticeService;

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 生成结果通知书
     *
     * @param projectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getResultNotice/{projectId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue getResultNotice(@Validated
                                       @PathVariable("projectId") String projectId) {
        //查询结果通知书模板的路径
        Attachment attachment = attachmentService.findByResultTemplate();

        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ResultNotice resultNotice = resultNoticeService.save(projectId, this.getUser(),attachment.getPath());
        responseBuilder.data(resultNotice);
        return responseBuilder.build();
    }


    /**
     * 采购结果通知书列表
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
    public ResponseValue getResultList(@Validated
                                         @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                         @RequestParam(value = "projectCode", required = false) String projectCode,
                                         @RequestParam(value = "projectSubject", required = false) String projectSubject,
                                         HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "resultId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<ResultNotice> specification = resultNoticeService.getWhereClause(projectCode, projectSubject);
        Page<ResultNotice> page = resultNoticeService.findContractNotice(specification, pageable);
        ResultNoticeCollectionDto resultNoticeCollectionDto = resultNoticeService.to(page, request);
        responseBuilder.data(resultNoticeCollectionDto);

        return responseBuilder.build();
    }


    /**
     * 查看采购结果通知书详情
     *
     * @param resultNoticeId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{resultNoticeId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getResultNoticeById(@Validated
                                               @PathVariable("resultNoticeId") String resultNoticeId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ResultNotice resultNotice = resultNoticeService.getResultNoticeById(resultNoticeId);
        responseBuilder.data(resultNotice);
        return responseBuilder.build();
    }

    /**
     * 导出
     * @param resultNoticeIds
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportContract(@RequestBody(required = false) List<Long> resultNoticeIds,
                               @Context HttpServletResponse response) {
        resultNoticeService.export(response, resultNoticeIds);
    }

    /**
     * 查看采购结果关联的项目
     *
     * @param resultNoticeId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getRelateProject/{resultNoticeId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getRelateProject(@Validated
                                             @PathVariable("resultNoticeId") String resultNoticeId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        ProjectInfo projectInfo = resultNoticeService.getRelateProject(resultNoticeId);
        responseBuilder.data(projectInfo);
        return responseBuilder.build();
    }
}
