package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.ResultNoticeCollectionDto;
import com.etone.protocolsupply.model.dto.notice.ResultNoticeDto;
import com.etone.protocolsupply.model.entity.AgentInfoExp;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.notice.ResultNotice;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.repository.notice.ResultNoticeRepository;
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

@Transactional(rollbackFor = Exception.class)
@Service
public class ResultNoticeService {

    @Autowired
    private ResultNoticeRepository resultNoticeRepository;

    @Autowired
    private PagingMapper pagingMapper;

    @Autowired
    private ProjectInfoRepository projectInfoRepository;


    public Specification<ResultNotice> getWhereClause(String projectCode, String projectSubject) {
        return (Specification<ResultNotice>) (root, criteriaQuery, criteriaBuilder) -> {

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

    public Page<ResultNotice> findContractNotice(Specification<ResultNotice> specification, Pageable pageable) {
        return resultNoticeRepository.findAll(specification,pageable);
    }

    public ResultNoticeCollectionDto to(Page<ResultNotice> page, HttpServletRequest request) {
        ResultNoticeCollectionDto resultNoticeCollectionDto = new ResultNoticeCollectionDto();
        pagingMapper.storeMappedInstanceBefore(page, resultNoticeCollectionDto, request);
        ResultNoticeDto resultNoticeDto;
        for (ResultNotice resultNotice : page) {
            resultNoticeDto = new ResultNoticeDto();
            BeanUtils.copyProperties(resultNotice, resultNoticeDto);
            resultNoticeCollectionDto.add(resultNoticeDto);
        }
        return resultNoticeCollectionDto;
    }

    public ResultNotice getResultNoticeById(String resultNoticeId) {
        return resultNoticeRepository.findById(Long.parseLong(resultNoticeId)).get();
    }

    public void export(HttpServletResponse response, List<Long> resultNoticeIds) {
        try {
            String[] header = {"项目主题", "项目编号", "成交供应商", "成交金额", "状态", "采购人", "创建人", "创建时间", "签收时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("采购结果信息表");
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

            List<ResultNotice> list = new ArrayList<>();
            if (resultNoticeIds != null && !resultNoticeIds.isEmpty()) {
                list = resultNoticeRepository.findAll(resultNoticeIds);
            }
            ResultNotice resultNotice;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                resultNotice = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(resultNotice.getProjectSubject()));
                row.createCell(1).setCellValue(new HSSFRichTextString(resultNotice.getProjectCode()));
                row.createCell(2).setCellValue(new HSSFRichTextString(resultNotice.getSupplier()==null?"":resultNotice.getSupplier()));
                row.createCell(3).setCellValue(new HSSFRichTextString(resultNotice.getAmount()));
                row.createCell(4).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(resultNotice.getStatus())));
                row.createCell(5).setCellValue(new HSSFRichTextString(resultNotice.getPurchaser()));
                row.createCell(6).setCellValue(new HSSFRichTextString(resultNotice.getCreator()));
                row.createCell(7).setCellValue(new HSSFRichTextString(format.format(resultNotice.getCreateDate())));
                String signDate;
                if(resultNotice.getSignDate()==null){
                    signDate="";
                }else {
                    signDate=format.format(resultNotice.getSignDate());
                }
                row.createCell(8).setCellValue(new HSSFRichTextString(signDate));
            }
            response.setHeader("Content-disposition", "attachment;filename=resultNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProjectInfo getRelateProject(String resultNoticeId) {
        return resultNoticeRepository.findById(Long.parseLong(resultNoticeId)).get().getProjectInfo();
    }

    public ResultNotice save(String projectId, JwtUser user) {
        ProjectInfo projectInfo = projectInfoRepository.findAllByProjectId(Long.valueOf(projectId));
        ResultNotice resultNotice = new ResultNotice();
        resultNotice.setProjectCode(projectInfo.getProjectCode());
        resultNotice.setProjectSubject(projectInfo.getProjectSubject());
        resultNotice.setAmount(projectInfo.getAmount());
        /*for (AgentInfoExp agentInfoExp : projectInfo.getAgentInfoExps()) {
            if (Constant.RECOMMEND_SUPPLIER_YES == agentInfoExp.getIsRecommendSupplier().intValue()) {
                resultNotice.setSupplier(agentInfoExp.getAgentName());
            }
        }*/
        resultNotice.setStatus(Constant.STATE_WAIT_SIGN);
        resultNotice.setCreator(user.getFullname());
        resultNotice.setCreateDate(new Date());
        resultNotice.setPurchaser(projectInfo.getPurchaser());
        resultNotice.setProjectInfo(projectInfo);
        resultNoticeRepository.save(resultNotice);
        return resultNotice;
    }
}
