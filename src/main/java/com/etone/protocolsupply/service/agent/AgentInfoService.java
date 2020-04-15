package com.etone.protocolsupply.service.agent;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.AgentExpCollectionDto;
import com.etone.protocolsupply.model.dto.AgentInfoExpDto;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.agent.AgentCollectionDto;
import com.etone.protocolsupply.model.dto.agent.AgentInfoDto;
import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.AgentInfoExp;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.PartInfoExp;
import com.etone.protocolsupply.repository.AgentInfoRepository;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.template.AgentInfoExpRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import com.etone.protocolsupply.utils.SpringUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
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
public class AgentInfoService {

    @Autowired
    private AgentInfoRepository  agentInfoRepository;
    @Autowired
    private AgentInfoExpRepository agentInfoExpRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private PagingMapper         pagingMapper;

    public AgentInfo save(AgentInfoDto agentInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();

        Attachment attachment = agentInfoDto.getAttachment();

        AgentInfo agentInfo = new AgentInfo();
        BeanUtils.copyProperties(agentInfoDto, agentInfo);
        agentInfo.setReviewStatus(Constant.STATE_DRAFT);
        agentInfo.setCreator(userName);
        agentInfo.setCreateDate(date);
        agentInfo.setIsDelete(Constant.DELETE_NO);
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                agentInfo.setAttachment(optional.get());
            }
        }
        return agentInfoRepository.save(agentInfo);
    }

    public Specification<AgentInfo> getWhereClause(String agentName, String status, String isDelete) {
        return (Specification<AgentInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(agentName)) {
                predicates.add(criteriaBuilder.equal(root.get("agentName").as(String.class), agentName));
            }
            if (Strings.isNotBlank(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status").as(String.class), status));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<AgentInfo> findAgents(Specification<AgentInfo> specification, Pageable pageable) {
        return agentInfoRepository.findAll(specification, pageable);
    }

    //项目-代理商列表
    public Page<AgentInfoExp> findAgentExps(String projectId,String isDelete, Pageable pageable) {
        return Common.listConvertToPage(agentInfoExpRepository.findAll(projectId, isDelete), pageable);
    }

    public AgentCollectionDto to(Page<AgentInfo> source, HttpServletRequest request) {
        AgentCollectionDto agentCollectionDto = new AgentCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, agentCollectionDto, request);
        AgentInfoDto agentInfoDto;
        for (AgentInfo agentInfo : source) {
            agentInfoDto = new AgentInfoDto();
            BeanUtils.copyProperties(agentInfo, agentInfoDto);
            agentCollectionDto.add(agentInfoDto);
        }
        return agentCollectionDto;
    }
    //项目-代理商列表
    public AgentExpCollectionDto toExp(Page<AgentInfoExp> source, HttpServletRequest request) {
        AgentExpCollectionDto agentExpCollectionDto = new AgentExpCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, agentExpCollectionDto, request);
        AgentInfoExpDto agentInfoExpDto;
        for (AgentInfoExp agentInfoExp : source) {
            agentInfoExpDto = new AgentInfoExpDto();
            BeanUtils.copyProperties(agentInfoExp, agentInfoExpDto);
            agentExpCollectionDto.add(agentInfoExpDto);
        }
        return agentExpCollectionDto;
    }

    public AgentInfo findOne(Long agentId) {
        Optional<AgentInfo> optional = agentInfoRepository.findById(agentId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过代理商ID"));
        }
    }

    public AgentInfo update(AgentInfoDto agentInfoDto) throws GlobalServiceException {
        AgentInfo agentInfo = this.findOne(agentInfoDto.getAgentId());
        Attachment attachment = agentInfoDto.getAttachment();
        SpringUtil.copyPropertiesIgnoreNull(agentInfoDto, agentInfo);
        if (agentInfo != null && attachment == null) {
            agentInfoRepository.save(agentInfo);
        }
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                agentInfo.setAttachment(optional.get());
            }
            agentInfoRepository.save(agentInfo);
        }
        return agentInfo;
    }

    public void delete(Long agentId) {
        agentInfoRepository.updateIsDelete(agentId);
    }

    public void export(HttpServletResponse response, String agentName, String status, String isDelete, List<Long> agentIds) {
        try {
            String[] header = {"代理商名称", "代理费用扣点（百分比）", "状态", "厂家授权函", "审核状态", "创建人", "创建时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("代理商信息表");
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

            List<AgentInfo> list;
            if (agentIds != null && !agentIds.isEmpty()) {
                list = agentInfoRepository.findAll(agentName, status, agentIds);
            } else {
                list = agentInfoRepository.findAll(agentName, status);
            }
            AgentInfo agentInfo;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                agentInfo = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(agentInfo.getAgentName()));
                row.createCell(1).setCellValue(new HSSFRichTextString(agentInfo.getAgentPoint()));
                row.createCell(2).setCellValue(new HSSFRichTextString(Constant.STATUS_MAP.get(agentInfo.getStatus())));
                row.createCell(3).setCellValue(new HSSFRichTextString(agentInfo.getAttachment().getAttachName()));
                row.createCell(4).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(agentInfo.getReviewStatus())));
                row.createCell(5).setCellValue(new HSSFRichTextString(agentInfo.getCreator()));
                row.createCell(6).setCellValue(new HSSFRichTextString(format.format(agentInfo.getCreateDate())));
            }
            response.setHeader("Content-disposition", "attachment;filename=agentInfo.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
