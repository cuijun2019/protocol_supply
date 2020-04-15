package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.ContractNoticceDto;
import com.etone.protocolsupply.model.dto.notice.ContractNoticeCollectionDto;
import com.etone.protocolsupply.model.entity.AgentInfoExp;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.notice.ContractNotice;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.repository.notice.ContractNoticeRepository;
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
public class ContractNoticeService {

    @Autowired
    private ContractNoticeRepository ContractNoticeRepository;

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Autowired
    private PagingMapper pagingMapper;

    public Specification<ContractNotice> getWhereClause(String projectCode, String projectSubject) {
        return (Specification<ContractNotice>) (root, criteriaQuery, criteriaBuilder) -> {

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

    public Page<ContractNotice> findContractNotice(Specification<ContractNotice> specification, Pageable pageable) {
        return ContractNoticeRepository.findAll(specification,pageable);
    }

    public ContractNoticeCollectionDto to(Page<ContractNotice> page, HttpServletRequest request) {
        ContractNoticeCollectionDto contractNoticeCollectionDto = new ContractNoticeCollectionDto();
        pagingMapper.storeMappedInstanceBefore(page, contractNoticeCollectionDto, request);
        ContractNoticceDto contractNoticceDto;
        for (ContractNotice contractNotice : page) {
            contractNoticceDto = new ContractNoticceDto();
            BeanUtils.copyProperties(contractNotice, contractNoticceDto);
            contractNoticeCollectionDto.add(contractNoticceDto);
        }
        return contractNoticeCollectionDto;
    }

    public ContractNotice update(String contractNoticeId) {
        Long contractId = Long.valueOf(contractNoticeId);
        Optional<ContractNotice> optional = ContractNoticeRepository.findById(contractId);
        ContractNotice contractNotice = new ContractNotice();
        if (optional.isPresent()) {
            contractNotice = optional.get();
            contractNotice.setStatus(Constant.STATE_SIGNED);
            contractNotice.setSignDate(new Date());
            ContractNoticeRepository.save(contractNotice);
        }
        return contractNotice;
    }

    public void export(HttpServletResponse response, List<Long> contractNoticeIds) {
        try {
            String[] header = {"项目主题", "项目编号", "成交供应商", "成交金额", "状态", "采购人", "创建人", "创建时间", "签收时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("合同信息表");
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

            List<ContractNotice> list = new ArrayList<>();
            if (contractNoticeIds != null && !contractNoticeIds.isEmpty()) {
                list = ContractNoticeRepository.findAll(contractNoticeIds);
            }
            ContractNotice contractNotice;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                contractNotice = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(contractNotice.getProjectSubject()));
                row.createCell(1).setCellValue(new HSSFRichTextString(contractNotice.getProjectCode()));
                row.createCell(2).setCellValue(new HSSFRichTextString(contractNotice.getSupplier()==null?"":contractNotice.getSupplier()));
                row.createCell(3).setCellValue(new HSSFRichTextString(contractNotice.getAmount()));
                row.createCell(4).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(contractNotice.getStatus())));
                row.createCell(5).setCellValue(new HSSFRichTextString(contractNotice.getPurchaser()));
                row.createCell(6).setCellValue(new HSSFRichTextString(contractNotice.getCreator()));
                row.createCell(7).setCellValue(new HSSFRichTextString(format.format(contractNotice.getCreateDate())));
                String signDate;
                if(contractNotice.getSignDate()==null){
                    signDate="";
                }else {
                    signDate=format.format(contractNotice.getSignDate());
                }
                row.createCell(8).setCellValue(new HSSFRichTextString(signDate));
            }
            response.setHeader("Content-disposition", "attachment;filename=contractNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ContractNotice getContractNoticeById(String contractNoticeId) {
        return ContractNoticeRepository.findById(Long.parseLong(contractNoticeId)).get();
    }

    public ContractNotice save(String projectId, JwtUser user) {
        ProjectInfo projectInfo = projectInfoRepository.findAllByProjectId(Long.valueOf(projectId));
        ContractNotice contractNotice = new ContractNotice();
        contractNotice.setProjectCode(projectInfo.getProjectCode());
        contractNotice.setProjectSubject(projectInfo.getProjectSubject());
        contractNotice.setAmount(projectInfo.getAmount());
        /*for (AgentInfoExp agentInfoExp : projectInfo.getAgentInfoExps()) {
            if (Constant.RECOMMEND_SUPPLIER_YES == agentInfoExp.getIsRecommendSupplier().intValue()) {
                contractNotice.setSupplier(agentInfoExp.getAgentName());
            }
        }*/
        contractNotice.setStatus(Constant.STATE_WAIT_SIGN);
        contractNotice.setCreator(user.getFullname());
        contractNotice.setCreateDate(new Date());
        contractNotice.setPurchaser(projectInfo.getPurchaser());
        contractNotice.setProjectInfo(projectInfo);
        ContractNoticeRepository.save(contractNotice);
        return contractNotice;
    }
}
