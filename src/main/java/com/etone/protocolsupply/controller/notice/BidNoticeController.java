package com.etone.protocolsupply.controller.notice;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.notice.BidNoticeCollectionDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.service.notice.BidNoticeService;
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
@RequestMapping(value = "${jwt.route.path}/bidNotice")
public class BidNoticeController extends GenericController {

    @Autowired
    private BidNoticeService bidNoticeService;

    @Autowired
    private AttachmentService attachmentService;


    /**
     * 生成成交通知书
     *
     * @param projectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBidNotice/{projectId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue getBidNotice(@Validated
                                       @PathVariable("projectId") String projectId) {
        //查看成交通知书模板路径
        //Attachment attachment =  attachmentService.findBidTemplate();

        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BidNotice bidNotice = bidNoticeService.save(projectId, this.getUser());
        responseBuilder.data(bidNotice);
        return responseBuilder.build();
    }

    /**
     * 成交通知书列表
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
    public ResponseValue getCargoInfos(@Validated
                                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "projectCode", required = false) String projectCode,
                                       @RequestParam(value = "projectSubject", required = false) String projectSubject,
                                       @RequestParam(value = "status", required = false) String status,
                                       HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Sort sort = new Sort(Sort.Direction.DESC, "bidId");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        //Specification<BidNotice> specification = bidNoticeService.getWhereClause(projectCode, projectSubject,status);
        Page<BidNotice> page = bidNoticeService.findMyBidNotices(projectCode, projectSubject,status,this.getUser(), pageable);
        BidNoticeCollectionDto bidNoticeCollectionDto = bidNoticeService.to(page, request);
        responseBuilder.data(bidNoticeCollectionDto);

        return responseBuilder.build();
    }

    /**
     * 处理（修改状态）
     *
     * @param projectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateStatus/{projectId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateBidTemplate(@PathVariable("projectId") String projectId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BidNotice bidNotice = bidNoticeService.update(projectId);
        responseBuilder.data(bidNotice);
        return responseBuilder.build();
    }

    /**
     * 导出
     *
     * @param projectCode
     * @param projectSubject
     * @param bidNoticeIds
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/export",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public void exportAgent(@RequestParam(value = "projectCode", required = false) String projectCode,
                            @RequestParam(value = "projectSubject", required = false) String projectSubject,
                            @RequestBody(required = false) List<Long> bidNoticeIds,
                            @Context HttpServletResponse response) {
        bidNoticeService.export(response, bidNoticeIds);
    }


    /**
     * 查看中标通知书详情
     *
     * @param bidNoticeId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{bidNoticeId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getBidNoticeById(@Validated
                                          @PathVariable("bidNoticeId") String bidNoticeId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        BidNotice bidNotice = bidNoticeService.getBidNoticeById(bidNoticeId);
        responseBuilder.data(bidNotice);
        return responseBuilder.build();
    }
}
