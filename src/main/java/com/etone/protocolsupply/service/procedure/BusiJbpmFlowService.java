package com.etone.protocolsupply.service.procedure;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.agent.AgentCollectionDto;
import com.etone.protocolsupply.model.dto.agent.AgentInfoDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoRepository;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import com.etone.protocolsupply.utils.Common;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class BusiJbpmFlowService {

    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private BusiJbpmFlowRepository busiJbpmFlowRepository;
    @Autowired
    private PartnerInfoRepository partnerInfoRepository;
    @Autowired
    private PagingMapper         pagingMapper;


    public BusiJbpmFlow save(BusiJbpmFlowDto busiJbpmFlowDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        BusiJbpmFlow busiJbpmFlow = new BusiJbpmFlow();
        BeanUtils.copyProperties(busiJbpmFlowDto, busiJbpmFlow);
        busiJbpmFlow.setFlowStartTime(date);//询价时间
        busiJbpmFlow.setFlowInitorId(userName);
        busiJbpmFlow = busiJbpmFlowRepository.save(busiJbpmFlow);
        return busiJbpmFlow;
    }

    public Specification<BusiJbpmFlow> getWhereClause(String businessType, String businessSubject ) {
        return (Specification<BusiJbpmFlow>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(businessType)) {
                predicates.add(criteriaBuilder.equal(root.get("businessType").as(String.class), businessType));
            }
            if (Strings.isNotBlank(businessSubject)) {
                predicates.add(criteriaBuilder.like(root.get("businessSubject").as(String.class), '%'+businessSubject+'%'));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<BusiJbpmFlow> findAgents(Specification<BusiJbpmFlow> specification, Pageable pageable) {
        return busiJbpmFlowRepository.findAll(specification, pageable);
    }

    public BusiJbpmFlowCollectionDto to(Page<BusiJbpmFlow> source, HttpServletRequest request,JwtUser jwtUser) {
        BusiJbpmFlowCollectionDto busiJbpmFlowCollectionDto = new BusiJbpmFlowCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, busiJbpmFlowCollectionDto, request);
        BusiJbpmFlowDto busiJbpmFlowDto;
        for (BusiJbpmFlow busiJbpmFlow : source) {
            busiJbpmFlowDto = new BusiJbpmFlowDto();
            BeanUtils.copyProperties(busiJbpmFlow, busiJbpmFlowDto);
            busiJbpmFlowDto.setParentActor(jwtUser.getUsername());
            busiJbpmFlowCollectionDto.add(busiJbpmFlowDto);
        }
        return busiJbpmFlowCollectionDto;
    }

    public void export(HttpServletResponse response, String businessType, String businessSubject, List<Long> ids,JwtUser jwtUser) {
        try {
            String[] header = {"待办类型", "待办主题", "状态", "当前处理人", "创建人", "创建时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("待办信息表");
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

            List<BusiJbpmFlow> list;
            if (ids != null && !ids.isEmpty()) {
                list = busiJbpmFlowRepository.findAll(businessType, businessSubject, ids);
            } else {
                list = busiJbpmFlowRepository.findAll(businessType, businessSubject);
            }
            BusiJbpmFlow busiJbpmFlow;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                busiJbpmFlow = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(Constant.BUSINESS_TYPE_STATUS_MAP.get(busiJbpmFlow.getBusinessType())));
                row.createCell(1).setCellValue(new HSSFRichTextString(busiJbpmFlow.getBusinessSubject()));
                row.createCell(2).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(Integer.parseInt(busiJbpmFlow.getTaskState()))));
                row.createCell(3).setCellValue(new HSSFRichTextString(jwtUser.getUsername()));
                row.createCell(4).setCellValue(new HSSFRichTextString(busiJbpmFlow.getFlowInitorId()));
                row.createCell(5).setCellValue(new HSSFRichTextString(format.format(busiJbpmFlow.getFlowStartTime())));
            }
            response.setHeader("Content-disposition", "attachment;filename=busiJbpmFlow.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
