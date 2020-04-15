package com.etone.protocolsupply.service.project;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.project.AgentInfoExpRepository;
import com.etone.protocolsupply.repository.project.PartInfoExpRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class ProjectInfoService {

    @Autowired
    private ProjectInfoRepository  projectInfoRepository;
    @Autowired
    private CargoInfoRepository    cargoInfoRepository;
    @Autowired
    private AttachmentRepository   attachmentRepository;
    @Autowired
    private AgentInfoExpRepository agentInfoExpRepository;
    @Autowired
    private PartInfoExpRepository  partInfoExpRepository;
    @Autowired
    private PagingMapper           pagingMapper;


    public ProjectInfo save(ProjectInfoDto projectInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        ProjectInfo projectInfo = new ProjectInfo();
        BeanUtils.copyProperties(projectInfoDto, projectInfo);
        String maxOne = projectInfoRepository.findMaxOne();
        if (maxOne == null) {
            projectInfo.setProjectCode("SCUT-" + Common.getYYYYMMDDDate(date) + "-XY001");
        } else {
            ProjectInfo projectInfo1 = projectInfoRepository.findAllByProjectId(Long.parseLong(maxOne));
            projectInfo.setProjectCode("SCUT-" + Common.getYYYYMMDDDate(date) + "-XY" + Common.convertSerialProject(projectInfo1.getProjectCode().substring(16), 1));

        }
        projectInfo.setIsDelete(Constant.DELETE_NO);
        projectInfo.setCreator(userName);
        projectInfo.setStatus(1);//审核状态：审核中、已完成、退回

        Attachment attachment = projectInfoDto.getAttachment_n();//中标通知书
        if (attachment != null && attachment.getAttachId() != null && !attachment.getAttachId().equals("")) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                projectInfo.setAttachment_n(optional.get());
            }
        } else {
            projectInfo.setAttachment_n(null);
        }
        Attachment attachment_c = projectInfoDto.getAttachment_c();//合同
        if (attachment_c != null && attachment_c.getAttachId() != null && !attachment_c.getAttachId().equals("")) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment_c.getAttachId());
            if (optional.isPresent()) {
                projectInfo.setAttachment_c(optional.get());
            }
        } else {
            projectInfo.setAttachment_c(null);
        }

        projectInfo.setProjectSubject(projectInfoDto.getCargoName() + "的采购");

        //配件
        Set<PartInfoExp> partInfoExps = projectInfoDto.getPartInfoExps();
        if (partInfoExps != null && !partInfoExps.isEmpty()) {
            for (PartInfoExp partInfoExp : partInfoExps) {
                partInfoExp.setIsDelete(Constant.DELETE_NO);
                partInfoExpRepository.save(partInfoExp);
            }
        }
        //代理商list
        Set<AgentInfoExp> agentInfoExps = projectInfoDto.getAgentInfoExps();
        if (agentInfoExps != null && !agentInfoExps.isEmpty()) {
            for (AgentInfoExp agentInfoExp : agentInfoExps) {
                agentInfoExp.setCreator(userName);
                agentInfoExp.setCreateDate(date);
                agentInfoExp.setStatus(1);//状态
                agentInfoExp.setReviewStatus(1);//审核状态
                agentInfoExp.setIsDelete(Constant.DELETE_NO);
                agentInfoExpRepository.save(agentInfoExp);
            }
        }

        projectInfoRepository.save(projectInfo);

        List<Long> agentIds = new ArrayList<>();
        if (agentInfoExps.size() > 0) {
            for (AgentInfoExp agentInfoExp : projectInfoDto.getAgentInfoExps()) {
                agentIds.add(agentInfoExp.getAgentId());
            }
            agentInfoExpRepository.setProjectId(projectInfo.getProjectId(), agentIds);
        }
        List<Long> partIds = new ArrayList<>();
        if (partInfoExps.size() > 0) {
            for (PartInfoExp partInfoExp : projectInfoDto.getPartInfoExps()) {
                partIds.add(partInfoExp.getPartId());
            }
            partInfoExpRepository.setProjectId(projectInfo.getProjectId(), partIds);
        }
        return projectInfo;
    }


    public Specification<ProjectInfo> getWhereClause(String projectSubject, String status, String isDelete) {
        return (Specification<ProjectInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(projectSubject)) {
                predicates.add(criteriaBuilder.like(root.get("projectSubject").as(String.class), '%' + projectSubject + '%'));
            }
            if (Strings.isNotBlank(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status").as(String.class), status));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<ProjectInfo> findAgents(Specification<ProjectInfo> specification, Pageable pageable) {
        return projectInfoRepository.findAll(specification, pageable);
    }

    public ProjectCollectionDto to(Page<ProjectInfo> source, HttpServletRequest request) {
        ProjectCollectionDto projectCollectionDto = new ProjectCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, projectCollectionDto, request);
        ProjectInfoDto projectInfoDto;
        for (ProjectInfo projectInfo : source) {
            CargoInfo cargoInfo = cargoInfoRepository.findAllByProjectId(projectInfo.getProjectId());
            projectInfoDto = new ProjectInfoDto();
            BeanUtils.copyProperties(projectInfo, projectInfoDto);
            projectInfoDto.setCargoId(cargoInfo.getCargoId().toString());//货物id
            projectInfoDto.setCargoName(cargoInfo.getCargoName());//货物名称
            projectInfoDto.setCurrency(cargoInfo.getCurrency());//币种
            projectInfoDto.setGuaranteeRate(cargoInfo.getGuaranteeRate());//维保率
            projectCollectionDto.add(projectInfoDto);
        }
        return projectCollectionDto;
    }

    public ProjectInfo update(ProjectInfoDto projectInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        String username = jwtUser.getUsername();
        ProjectInfo projectInfo = this.findOne(projectInfoDto.getProjectId());
        Attachment attachmentn = projectInfoDto.getAttachment_n();//中标通知书
        Attachment attachmentc = projectInfoDto.getAttachment_c();//合同
        CargoInfo cargoInfo = cargoInfoRepository.findAllByCargoId(Long.parseLong(projectInfoDto.getCargoId()));//货物
        SpringUtil.copyPropertiesIgnoreNull(projectInfoDto, projectInfo);
        if (projectInfo != null && attachmentn == null && attachmentc == null && cargoInfo == null) {
            projectInfoRepository.save(projectInfo);
        }
        if (attachmentn != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachmentn.getAttachId());
            if (optional.isPresent()) {
                projectInfo.setAttachment_n(optional.get());
            }
        }
        if (attachmentc != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachmentc.getAttachId());
            if (optional.isPresent()) {
                projectInfo.setAttachment_c(optional.get());
            }
        }

        //agentInfoExpRepository.deleteByProjectId(projectInfoDto.getProjectId());
        //供应商
//        Set<AgentInfoExp> agentInfoExps=projectInfoDto.getAgentInfoExps();
//        if (agentInfoExps != null && !agentInfoExps.isEmpty()) {
//            for (AgentInfoExp agentInfoExp : agentInfoExps) {
//                agentInfoExp.setReviewStatus(1);
//                agentInfoExp.setIsDelete(Constant.DELETE_NO);
//                agentInfoExp.setCreateDate(new Date());
//                agentInfoExp.setCreator(username);
//            }
//        }
//       // partInfoExpRepository.deleteByProjectId(projectInfoDto.getProjectId());
//        //货物配件
//        Set<PartInfoExp>partInfoExps=projectInfoDto.getPartInfoExps();
//        if (partInfoExps != null && !partInfoExps.isEmpty()) {
//            for (PartInfoExp partInfoExp : partInfoExps) {
//                partInfoExp.setIsDelete(Constant.DELETE_NO);
//
//            }
//        }

        projectInfoRepository.save(projectInfo);
        return projectInfo;
    }

    public ProjectInfo findOne(Long projectId) {
        Optional<ProjectInfo> optional = projectInfoRepository.findById(projectId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过项目id"));
        }
    }

    public void delete(Long projectId) {
        projectInfoRepository.updateIsDelete(projectId);
    }

    public void export(HttpServletResponse response, String projectSubject, String status, String isDelete, List<Long> projectIds) {
        try {
            String[] header = {"项目主题", "项目编号", "货物名称", "货物金额", "项目总金额", "币种", "状态",
                    "采购结果通知书", "中标通知书", "合同"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("项目信息表");
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

            List<ProjectInfo> list;
            if (projectIds != null && !projectIds.isEmpty()) {
                list = projectInfoRepository.findAllp(projectSubject, status, projectIds);
            } else {
                list = projectInfoRepository.findAllp2(projectSubject, status);
            }
            ProjectInfo projectInfo;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                projectInfo = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                Attachment attachmentn = projectInfo.getAttachment_n();//中标通知书
                Attachment attachmentc = projectInfo.getAttachment_c();//合同
                if (attachmentn != null) {
                    attachmentn = attachmentRepository.getOne(projectInfo.getAttachment_n().getAttachId());
                    row.createCell(8).setCellValue(new HSSFRichTextString(attachmentn.getAttachName()));
                } else {
                    row.createCell(8).setCellValue(new HSSFRichTextString(""));
                }
                if (attachmentc != null) {
                    attachmentc = attachmentRepository.getOne(projectInfo.getAttachment_c().getAttachId());
                    row.createCell(9).setCellValue(new HSSFRichTextString(attachmentc.getAttachName()));
                } else {
                    row.createCell(9).setCellValue(new HSSFRichTextString(""));
                }
                CargoInfo cargoInfo = cargoInfoRepository.findAllByProjectId(projectInfo.getProjectId());
                row.createCell(0).setCellValue(new HSSFRichTextString(projectInfo.getProjectSubject()));
                row.createCell(1).setCellValue(new HSSFRichTextString(projectInfo.getProjectCode()));
                row.createCell(2).setCellValue(new HSSFRichTextString(cargoInfo.getCargoName()));
                row.createCell(3).setCellValue(new HSSFRichTextString("5000"));//货物金额
                row.createCell(4).setCellValue(new HSSFRichTextString("6000"));//项目总金额
                row.createCell(5).setCellValue(new HSSFRichTextString(cargoInfo.getCurrency()));//币种
                row.createCell(6).setCellValue(new HSSFRichTextString(projectStatus(projectInfo.getStatus())));//状态
                row.createCell(7).setCellValue(new HSSFRichTextString(""));//采购结果通知书
                //row.createCell(10).setCellValue(new HSSFRichTextString("6000"));//中标通知书
                // row.createCell(11).setCellValue(new HSSFRichTextString("6000"));//合同

            }
            response.setHeader("Content-disposition", "attachment;filename=projectInfo.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String projectStatus(int status) {
        String str = "";
        if (status == 1) {
            str = "审核中";
        }
        if (status == 2) {
            str = "已完成";
        }
        if (status == 3) {
            str = "已退回";
        }
        return str;
    }
}
