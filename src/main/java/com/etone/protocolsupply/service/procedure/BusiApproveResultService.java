package com.etone.protocolsupply.service.procedure;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.procedure.BusiApproveResultCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiApproveResultDto;
import com.etone.protocolsupply.model.entity.procedure.BusiApproveResult;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.procedure.BusiApproveResultRepository;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
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

@Transactional(rollbackFor = Exception.class)
@Service
public class BusiApproveResultService {

    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private BusiJbpmFlowRepository busiJbpmFlowRepository;
    @Autowired
    private BusiApproveResultRepository busiApproveResultRepository;
    @Autowired
    private PartnerInfoRepository partnerInfoRepository;
    @Autowired
    private PagingMapper         pagingMapper;


    public BusiApproveResult save(BusiApproveResultDto busiApproveResultDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        BusiApproveResult busiApproveResult = new BusiApproveResult();
        BeanUtils.copyProperties(busiApproveResultDto, busiApproveResult);
        busiApproveResult.setApproveStartTime(date);//已办时间
        busiApproveResult.setApproveInitorId(userName);//创建人
        busiApproveResult = busiApproveResultRepository.save(busiApproveResult);
        return busiApproveResult;
    }

    public Specification<BusiApproveResult> getWhereClause(String approveType, String approveSubject ) {
        return (Specification<BusiApproveResult>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(approveType)) {
                predicates.add(criteriaBuilder.equal(root.get("approveType").as(String.class), approveType));
            }
            if (Strings.isNotBlank(approveSubject)) {
                predicates.add(criteriaBuilder.like(root.get("approveSubject").as(String.class), '%'+approveSubject+'%'));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<BusiApproveResult> findbusiApproveResults(Specification<BusiApproveResult> specification, Pageable pageable) {
        return busiApproveResultRepository.findAll(specification, pageable);
    }

    public BusiApproveResultCollectionDto to(Page<BusiApproveResult> source, HttpServletRequest request, JwtUser jwtUser) {
        BusiApproveResultCollectionDto busiApproveResultCollectionDto = new BusiApproveResultCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, busiApproveResultCollectionDto, request);
        BusiApproveResultDto busiApproveResultDto;
        for (BusiApproveResult busiApproveResult : source) {
            busiApproveResultDto = new BusiApproveResultDto();
            BeanUtils.copyProperties(busiApproveResult, busiApproveResultDto);
            busiApproveResultDto.setParentActor(jwtUser.getUsername());
            busiApproveResultCollectionDto.add(busiApproveResultDto);
        }
        return busiApproveResultCollectionDto;
    }

    public void export(HttpServletResponse response, List<Long> ids,JwtUser jwtUser) {
        try {
            String[] header = {"已办类型", "已办主题", "状态", "当前处理人", "创建人", "创建时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("已办信息表");
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

            List<BusiApproveResult> list;
            if (ids != null && !ids.isEmpty()) {
                list = busiApproveResultRepository.findAll(ids);
            } else {
                list = busiApproveResultRepository.findAll();
            }
            BusiApproveResult busiApproveResult;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                busiApproveResult = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(Constant.BUSINESS_TYPE_STATUS_MAP.get(busiApproveResult.getApproveType())));
                row.createCell(1).setCellValue(new HSSFRichTextString(busiApproveResult.getApproveSubject()));
                row.createCell(2).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(Integer.parseInt(busiApproveResult.getApproveState()))));
                row.createCell(3).setCellValue(new HSSFRichTextString(jwtUser.getUsername()));
                row.createCell(4).setCellValue(new HSSFRichTextString(busiApproveResult.getApproveInitorId()));
                row.createCell(5).setCellValue(new HSSFRichTextString(format.format(busiApproveResult.getApproveStartTime())));
            }
            response.setHeader("Content-disposition", "attachment;filename=busiApproveResult.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
