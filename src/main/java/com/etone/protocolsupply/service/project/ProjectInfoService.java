package com.etone.protocolsupply.service.project;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoRepository;
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
    private InquiryInfoRepository inquiryInfoRepository;
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
       // projectInfo.setStatus(1);//审核状态：草稿、审核中、已完成、退回
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
        Attachment attachment_p = projectInfoDto.getAttachment_p();//采购结果通知书
        if (attachment_p != null && attachment_p.getAttachId() != null && !attachment_p.getAttachId().equals("")) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment_p.getAttachId());
            if (optional.isPresent()) {
                projectInfo.setAttachment_p(optional.get());
            }
        } else {
            projectInfo.setAttachment_p(null);
        }
        InquiryInfo inquiryInfo = projectInfoDto.getInquiryInfo();//关联询价
        if (inquiryInfo != null && inquiryInfo.getInquiryId() != null && !inquiryInfo.getInquiryId().equals("")) {
            Optional<InquiryInfo> optional = inquiryInfoRepository.findById(inquiryInfo.getInquiryId());
            projectInfo.getInquiryInfo().getCargoInfo().setPartInfos(null);

            if (optional.isPresent()) {
                projectInfo.setInquiryInfo(optional.get());
            }
        } else {
            projectInfo.setInquiryInfo(null);
        }

        projectInfo.setProjectSubject(projectInfoDto.getCargoName() + "的采购");
        Optional<CargoInfo> optional = cargoInfoRepository.findById(Long.parseLong(projectInfoDto.getCargoId()));
        if (optional.isPresent()) {
            projectInfoDto.setCargoInfo(optional.get());
        }
        //配件
        Set<PartInfoExp> partInfoExps = projectInfoDto.getPartInfoExps();
        if (partInfoExps != null && !partInfoExps.isEmpty()) {
            for (PartInfoExp partInfoExp : partInfoExps) {
                partInfoExp.setIsDelete(Constant.DELETE_NO);
                partInfoExp.setCargoInfo(projectInfoDto.getCargoInfo());
                partInfoExpRepository.save(partInfoExp);
            }
        }
         projectInfoRepository.save(projectInfo);
        List<Long> partIds = new ArrayList<>();
        if (partInfoExps.size() > 0) {
            for (PartInfoExp partInfoExp : projectInfoDto.getPartInfoExps()) {
                partIds.add(partInfoExp.getPartId());
            }
            partInfoExpRepository.setProjectId(projectInfo.getProjectId(), partIds);
        }
        //代理商list
        AgentInfoExp agentInfoExp = projectInfoDto.getAgentInfoExp();
        if (agentInfoExp != null ) {
            agentInfoExp.setCreator(userName);
            agentInfoExp.setCreateDate(date);
           // agentInfoExp.setStatus(1);//状态
            agentInfoExp.setReviewStatus(1);//审核状态
            agentInfoExp.setIsDelete(Constant.DELETE_NO);
            agentInfoExp.setProjectInfo(projectInfo);
            agentInfoExpRepository.save(agentInfoExp);
        }
        return projectInfo;
    }

    public Set<PartInfoExp> savePartExp(ProjectInfoDto projectInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        Optional<CargoInfo> optional = cargoInfoRepository.findById(Long.parseLong(projectInfoDto.getCargoId()));
        Optional<ProjectInfo> optionalProjectInfo = projectInfoRepository.findById(projectInfoDto.getProjectId());
        //配件
        Set<PartInfoExp> partInfoExps = projectInfoDto.getPartInfoExps();
        if (partInfoExps != null && !partInfoExps.isEmpty()) {
            for (PartInfoExp partInfoExp : partInfoExps) {
                partInfoExp.setIsDelete(Constant.DELETE_NO);
                if (optional.isPresent()) {
                    partInfoExp.setCargoInfo(optional.get());
                }
                if (optionalProjectInfo.isPresent()) {
                    partInfoExp.setProjectInfo(optionalProjectInfo.get());
                }
                partInfoExpRepository.save(partInfoExp);
                partInfoExp.getCargoInfo().setPartInfos(null);
                partInfoExp.setCargoInfo(null);
                partInfoExp.getProjectInfo().getInquiryInfo().getPartnerInfo().setContacts(null);
            }
        }

        return partInfoExps;
    }


    public Specification<ProjectInfo> getWhereClause(String projectSubject,String projectCode, String status,String inquiryId, String isDelete) {
        return (Specification<ProjectInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(projectSubject)) {
                predicates.add(criteriaBuilder.like(root.get("projectSubject").as(String.class), '%' + projectSubject + '%'));
            }
            if (Strings.isNotBlank(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status").as(String.class), status));
            }
//            if (Strings.isNotBlank(inquiryId)) {
//                predicates.add(criteriaBuilder.equal(root.get("inquiryId").as(Long.class), inquiryId));
//            }
            if (Strings.isNotBlank(projectCode)) {
                predicates.add(criteriaBuilder.like(root.get("projectCode").as(String.class), '%' + projectCode + '%'));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };

    }

    public Page<ProjectInfo> findProjectInfos(String isDelete, String projectSubject, String projectCode,String status,String inquiryId, Pageable pageable) {
        return Common.listConvertToPage(projectInfoRepository.findAll(isDelete, projectSubject, projectCode,status,inquiryId), pageable);
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
            cargoInfo.setPartInfos(null);
            projectInfoDto = new ProjectInfoDto();
            BeanUtils.copyProperties(projectInfo, projectInfoDto);
            projectInfoDto.setCargoId(cargoInfo.getCargoId().toString());//货物id
            projectInfoDto.setCargoName(cargoInfo.getCargoName());//货物名称
            projectInfoDto.setCurrency(cargoInfo.getCurrency());//币种
            projectInfoDto.setGuaranteeRate(cargoInfo.getGuaranteeRate());//维保率
            projectInfoDto.setCargoTotal(0.00);//货物总金额
            projectCollectionDto.add(projectInfoDto);
            projectInfoDto.getInquiryInfo().getPartnerInfo().setContacts(null);
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
        AgentInfoExp agentInfoExp=projectInfoDto.getAgentInfoExp();
        if (agentInfoExp != null ) {
                agentInfoExp.setReviewStatus(1);
                agentInfoExp.setIsDelete(Constant.DELETE_NO);
                agentInfoExp.setCreateDate(new Date());
                agentInfoExp.setCreator(username);
                agentInfoExp.setProjectInfo(projectInfo);
                agentInfoExpRepository.save(agentInfoExp);

        }
//       // partInfoExpRepository.deleteByProjectId(projectInfoDto.getProjectId());
        //货物配件
        Set<PartInfoExp>partInfoExps=projectInfoDto.getPartInfoExps();
        if (partInfoExps != null && !partInfoExps.isEmpty()) {
            for (PartInfoExp partInfoExp : partInfoExps) {
                partInfoExp.setIsDelete(Constant.DELETE_NO);
                partInfoExp.setProjectInfo(projectInfo);
                partInfoExpRepository.save(partInfoExp);
            }
        }

        projectInfoRepository.save(projectInfo);
        return projectInfo;
    }

    public ProjectInfoDto findOne(Long projectId) {
        Optional<ProjectInfo> optional = projectInfoRepository.findById(projectId);
        ProjectInfoDto projectInfoDto=new ProjectInfoDto();
        ProjectInfo projectInfo=new ProjectInfo();
        if (optional.isPresent()) {
             projectInfo=optional.get();
            BeanUtils.copyProperties(projectInfo, projectInfoDto);
        }
        AgentInfoExp agentInfoExp=agentInfoExpRepository.findByProjectId2(projectId);
        projectInfoDto.setAgentInfoExp(agentInfoExp);
        projectInfoDto.getInquiryInfo().getCargoInfo().setPartInfos(null);
        projectInfoDto.getInquiryInfo().getPartnerInfo().setContacts(null);
        return projectInfoDto;
    }

    public void delete(Long projectId) {
        projectInfoRepository.updateIsDelete(projectId);
    }

    public void export(HttpServletResponse response, List<Long> projectIds) {
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
                list = projectInfoRepository.findAllp(projectIds);
            } else {
                list = projectInfoRepository.findAll();
            }
            ProjectInfo projectInfo;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                projectInfo = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                Attachment attachmentp = projectInfo.getAttachment_p();//采购结果通知书
                Attachment attachmentn = projectInfo.getAttachment_n();//中标通知书
                Attachment attachmentc = projectInfo.getAttachment_c();//合同
                if (attachmentp != null) {
                    attachmentp = attachmentRepository.getOne(projectInfo.getAttachment_p().getAttachId());
                    row.createCell(7).setCellValue(new HSSFRichTextString(attachmentp.getAttachName()));
                } else {
                    row.createCell(7).setCellValue(new HSSFRichTextString(""));
                }
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
                row.createCell(6).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(projectInfo.getStatus())));//状态
               // row.createCell(7).setCellValue(new HSSFRichTextString(""));//采购结果通知书
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



}
