package com.etone.protocolsupply.service.project;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.ExcelHeaderColumnPojo;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.cargo.CargoCollectionDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.BrandItem;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.BrandItemRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.cargo.PartInfoRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.service.cargo.CargoInfoService;
import com.etone.protocolsupply.service.cargo.PartInfoService;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.json.JSONObject;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class ProjectInfoService {

    @Autowired
    private ProjectInfoRepository projectInfoRepository;
    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private PartInfoService      partInfoService;
    @Autowired
    private CargoInfoService cargoInfoService;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private PagingMapper         pagingMapper;

    @Autowired
    private BrandItemRepository brandItemRepository;

    public ProjectInfo save(ProjectInfoDto projectInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        ProjectInfo projectInfo = new ProjectInfo();
        BeanUtils.copyProperties(projectInfoDto, projectInfo);
        ProjectInfo projectInfo1=projectInfoRepository.findAlls();
        if(projectInfo1==null || "".equals(projectInfo1)){
            projectInfo.setProjectCode("SCUT"+Common.getYYYYMMDDDate(date)+"XY"+"001");
        }else {
            projectInfo.setProjectCode("SCUT"+Common.getYYYYMMDDDate(date)+"XY"+Common.convertSerialProject(projectInfo1.getProjectCode().substring(12),1));
        }

        projectInfo.setIsDelete(Constant.DELETE_NO);
        projectInfo.setCreator(userName);
        projectInfo.setStatus(1);//审核状态：审核中、已完成、退回

        Attachment attachment = projectInfoDto.getAttachment_n();//中标通知书
        if (attachment != null && attachment.getAttachId()!=null && !attachment.getAttachId().equals("")) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                projectInfo.setAttachment_n(optional.get());
            }
        }else {
            projectInfo.setAttachment_n(null);
        }
        Attachment attachment_c = projectInfoDto.getAttachment_c();//合同
        if (attachment_c != null && attachment_c.getAttachId()!=null && !attachment_c.getAttachId().equals("")) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment_c.getAttachId());
            if (optional.isPresent()) {
                projectInfo.setAttachment_c(optional.get());
            }
        }else {
            projectInfo.setAttachment_c(null);
        }
        CargoInfo cargoInfo=projectInfoDto.getCargoInfo();//货物
        CargoInfoDto cargoInfoDto=new CargoInfoDto();

        if (cargoInfo != null ) {
            BeanUtils.copyProperties(cargoInfo, cargoInfoDto);
            CargoInfo cargoInfo1=  cargoInfoService.save(cargoInfoDto, jwtUser);
            Optional<CargoInfo> optional = cargoInfoRepository.findById(cargoInfo1.getCargoId());
            if (optional.isPresent()) {
                projectInfo.setCargoInfo(optional.get());
                projectInfo.setProjectSubject(cargoInfo1.getCargoName()+"的采购");
            }
        }else {
            projectInfo.setCargoInfo(null);
        }

        return projectInfoRepository.save(projectInfo);
    }


    public Specification<ProjectInfo> getWhereClause(String projectSubject, String status, String isDelete) {
        return (Specification<ProjectInfo>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(projectSubject)) {
                predicates.add(criteriaBuilder.equal(root.get("projectSubject").as(String.class), projectSubject));
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
        ProjectInfoDto agentInfoDto;
        for (ProjectInfo agentInfo : source) {
            agentInfoDto = new ProjectInfoDto();
            BeanUtils.copyProperties(agentInfo, agentInfoDto);
            projectCollectionDto.add(agentInfoDto);
        }
        return projectCollectionDto;
    }

}
