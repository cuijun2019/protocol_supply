package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.BidNoticeCollectionDto;
import com.etone.protocolsupply.model.dto.notice.BidNoticeDto;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.repository.notice.BidNoticeRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class BidNoticeService {

    @Autowired
    private PagingMapper          pagingMapper;
    @Autowired
    private BidNoticeRepository   bidNoticeRepository;
    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    public BidNotice save(String projectId, JwtUser jwtUser) {
        Long proId = Long.valueOf(projectId);
        ProjectInfo projectInfo = projectInfoRepository.findAllByProjectId(proId);
        BidNotice bidNotice = new BidNotice();
        bidNotice.setProjectCode(projectInfo.getProjectCode());
        bidNotice.setProjectSubject(projectInfo.getProjectSubject());
        bidNotice.setAmount(projectInfo.getAmount());
        bidNotice.setSupplier(projectInfoRepository.getAgentName(proId));
        bidNotice.setStatus(Constant.STATE_WAIT_SIGN);
        bidNotice.setCreator(jwtUser.getFullname());
        bidNotice.setCreateDate(new Date());
        bidNotice.setPurchaser(projectInfo.getPurchaser());
        bidNotice.setProjectInfo(projectInfo);
        bidNoticeRepository.save(bidNotice);
        return bidNotice;
    }

    public BidNoticeCollectionDto to(Page<BidNotice> source, HttpServletRequest request) {
        BidNoticeCollectionDto bidNoticeCollectionDto = new BidNoticeCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, bidNoticeCollectionDto, request);
        BidNoticeDto bidNoticeDto;
        for (BidNotice bidNotice : source) {
            bidNoticeDto = new BidNoticeDto();
            BeanUtils.copyProperties(bidNotice, bidNoticeDto);
            bidNoticeCollectionDto.add(bidNoticeDto);
        }
        return bidNoticeCollectionDto;
    }

    public Specification<BidNotice> getWhereClause(String projectCode, String projectSubject) {
        return (Specification<BidNotice>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(projectCode)) {
                predicates.add(criteriaBuilder.equal(root.get("projectCode").as(String.class), projectCode));
            }
            if (Strings.isNotBlank(projectSubject)) {
                predicates.add(criteriaBuilder.equal(root.get("projectSubject").as(String.class), projectSubject));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<BidNotice> findBidNotices(Specification<BidNotice> specification, Pageable pageable) {
        return bidNoticeRepository.findAll(specification, pageable);
    }

    public BidNotice update(String bidNoticeId) {
        Long bidId = Long.valueOf(bidNoticeId);
        Optional<BidNotice> optional = bidNoticeRepository.findById(bidId);
        BidNotice bidNotice = new BidNotice();
        if (optional.isPresent()) {
            bidNotice = optional.get();
            bidNotice.setStatus(Constant.STATE_SIGNED);
            bidNotice.setSignDate(new Date());
            bidNoticeRepository.save(bidNotice);
        }
        return bidNotice;
    }

    public void export(HttpServletResponse response, String projectCode, String projectSubject, List<Long> bidNoticeIds) {
        try {
            String[] header = {"项目主题", "项目编号", "成交供应商", "成交金额", "状态", "采购人", "创建人", "创建时间", "签收时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("成交通知书信息表");
            sheet.setDefaultColumnWidth(10);
            //        创建标题的显示样式
            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //        创建第一行表头
            HSSFRow headrow = sheet.createRow(0);

            for (int i = 0; i < header.length; i++) {
                HSSFCell cell = headrow.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(header[i]);
                cell.setCellValue(text);
                cell.setCellStyle(headerStyle);
            }

            List<BidNotice> list;
            if (bidNoticeIds != null && !bidNoticeIds.isEmpty()) {
                //list = bidNoticeRepository.findAll(projectCode, projectSubject, bidNoticeIds);
                list = bidNoticeRepository.findAll(bidNoticeIds);
            } else {
                list = bidNoticeRepository.findAll(projectCode, projectSubject);
            }
            BidNotice bidNotice;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                bidNotice = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(bidNotice.getProjectSubject()));
                row.createCell(1).setCellValue(new HSSFRichTextString(bidNotice.getProjectCode()));
                row.createCell(2).setCellValue(new HSSFRichTextString(bidNotice.getSupplier() == null ? "" : bidNotice.getSupplier()));
                row.createCell(3).setCellValue(new HSSFRichTextString(bidNotice.getAmount()));
                row.createCell(4).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(bidNotice.getStatus())));
                row.createCell(5).setCellValue(new HSSFRichTextString(bidNotice.getPurchaser()));
                row.createCell(6).setCellValue(new HSSFRichTextString(bidNotice.getCreator()));
                row.createCell(7).setCellValue(new HSSFRichTextString(format.format(bidNotice.getCreateDate())));
                String signDate;
                if (bidNotice.getSignDate() == null) {
                    signDate = "";
                } else {
                    signDate = format.format(bidNotice.getSignDate());
                }
                row.createCell(8).setCellValue(new HSSFRichTextString(signDate));
            }
            response.setHeader("Content-disposition", "attachment;filename=bidNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public BidNotice getBidNoticeById(String bidNoticeId) {
        return bidNoticeRepository.findById(Long.parseLong(bidNoticeId)).get();
    }
}
