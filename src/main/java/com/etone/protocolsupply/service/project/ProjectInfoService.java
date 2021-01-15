package com.etone.protocolsupply.service.project;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.AgentInfoExpDto;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewDto;
import com.etone.protocolsupply.model.dto.project.ProjectCollectionDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoNewRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoRepository;
import com.etone.protocolsupply.repository.project.AgentInfoExpRepository;
import com.etone.protocolsupply.repository.project.PartInfoExpRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.service.inquiry.InquiryInfoNewService;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.formula.functions.BaseNumberUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ProjectInfoService.class);
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
    private InquiryInfoNewRepository inquiryInfoNewRepository;
    @Autowired
    private PagingMapper           pagingMapper;
    @Autowired
    private UserRepository userRepository;


    public ProjectInfo save(ProjectInfoDto projectInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        ProjectInfo projectInfo = new ProjectInfo();
        BeanUtils.copyProperties(projectInfoDto, projectInfo);
        String maxOne = projectInfoRepository.findMaxOne();
        ProjectInfo projectInfo1 = projectInfoRepository.findAllByProjectId(Long.parseLong(maxOne));
        //String sqlDate=projectInfo1.getProjectCode().substring(5,13);//取年月
        String sqlDate=projectInfo1.getProjectCode().substring(5,9);//只取年份
        if (Integer.parseInt(sqlDate)<Integer.parseInt(Common.getYYYYDate(date))) {
            projectInfo.setProjectCode("SCUT-" + Common.getYYYYDate(date) + "-XY001");
        } else {
//            ProjectInfo projectInfo1 = projectInfoRepository.findAllByProjectId(Long.parseLong(maxOne));
            projectInfo.setProjectCode("SCUT-" + Common.getYYYYDate(date) + "-XY" + Common.convertSerialProject(projectInfo1.getProjectCode().substring(12), 1));
        }
        projectInfo.setIsDelete(Constant.DELETE_NO);
//        User user = userRepository.findByUsername(userName);//需要修改-----
//        projectInfo.setCreator(user.getCompany());
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
        InquiryInfoNew inquiryInfoNew=inquiryInfoNewRepository.findAllByInquiryId(projectInfoDto.getInquiryInfo().getInquiryId());
            if (null!=inquiryInfoNew.getInquiryId()) {
                projectInfo.setInquiryId(projectInfoDto.getInquiryInfo().getInquiryId());
                projectInfo.setInquiryCode(projectInfoDto.getInquiryInfo().getInquiryCode());
            }
        projectInfo.setProjectSubject(projectInfoDto.getCargoName() + "的采购");
        Optional<CargoInfo> optional = cargoInfoRepository.findById(Long.parseLong(projectInfoDto.getCargoId()));
        if (optional.isPresent()) {
            projectInfoDto.setCargoInfo(optional.get());
        }

        projectInfoRepository.save(projectInfo);
//        List<Long> partIds = new ArrayList<>();
//        if (partInfoExps.size() > 0) {
//            for (PartInfoExp partInfoExp : projectInfoDto.getPartInfoExps()) {
//                partIds.add(partInfoExp.getPartId());
//            }
//            partInfoExpRepository.setProjectId(projectInfo.getProjectId(), partIds);
//        }
        //配件
        Set<PartInfoExp> partInfoExps = projectInfoDto.getPartInfoExps();
        if (partInfoExps != null && !partInfoExps.isEmpty()) {
            for (PartInfoExp partInfoExp : partInfoExps) {
                partInfoExp.setIsDelete(Constant.DELETE_NO);
                partInfoExp.setCargoInfo(projectInfoDto.getCargoInfo());
//                partInfoExpRepository.save(partInfoExp);
                partInfoExpRepository.savePartInfoExp(partInfoExp.getIsDelete()
                        ,partInfoExp.getManufactor()
                        ,partInfoExp.getPartCode()
                        ,partInfoExp.getPartName()
                        ,partInfoExp.getPartSerial()
                        ,partInfoExp.getPrice()
                        ,partInfoExp.getQuantity()
                        ,partInfoExp.getRemark()==null?"":partInfoExp.getRemark()
                        ,partInfoExp.getStandards()
                        ,partInfoExp.getTechParams()
                        ,partInfoExp.getTotal()
                        ,partInfoExp.getUnit()
                        ,projectInfo.getProjectId()
                        ,partInfoExp.getCargoInfo().getCargoId());
            }
        }
        //代理商list
        AgentInfoExp agentInfoExp = projectInfoDto.getAgentInfoExp();
        if (agentInfoExp != null ) {
            agentInfoExp.setCreator(userName);
            agentInfoExp.setCreateDate(date);
            agentInfoExp.setReviewStatus(1);//审核状态
            agentInfoExp.setIsDelete(Constant.DELETE_NO);
            agentInfoExp.setProjectInfo(projectInfo);
            agentInfoExpRepository.save(agentInfoExp);
        }

        return projectInfo;
    }
    public List<CargoInfo> getSetCargoInfo(String actor) throws GlobalServiceException {
        List<CargoInfo> cargoInfos= cargoInfoRepository.findAllByactor(actor,5,2);
        for(CargoInfo cargoInfo: cargoInfos){
            cargoInfo.setPartInfos(null);
        }
        return cargoInfos;
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
            }
        }

        return partInfoExps;
    }


    public Specification<ProjectInfo> getWhereClause(
            String projectSubject,String projectCode, String status,String inquiryId, String isDelete) {
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

    public Page<ProjectInfo> findMyProjectInfos(String isDelete, String projectSubject, String projectCode,String status,
                                                String inquiryId,JwtUser actor, Pageable pageable) {
        //判断当前用户是什么角色，如果是招标中心经办人或者招标科长或者admin则查询全部项目
        Long roleId = userRepository.findRoleIdByUsername(actor.getUsername());
        if( "5".equals(roleId+"") || "6".equals(roleId+"")|| "7".equals(roleId+"")){
               return Common.listConvertToPage(projectInfoRepository.findAll(isDelete, projectSubject, projectCode,status,inquiryId), pageable);
           }else {
               return Common.listConvertToPage(projectInfoRepository.findAll(isDelete, projectSubject, projectCode,status,inquiryId,actor.getUsername()), pageable);
           }
    }

    public Page<ProjectInfo> findAllByBusiJbpmFlow(String isDelete, String businessType, String parentActor, String status,JwtUser user, Pageable pageable) {
        //判断当前用户是什么角色，如果是admin则查询全部待办
        Long roleId = userRepository.findRoleIdByUsername(user.getUsername());
        if( "7".equals(roleId+"")){
            return Common.listConvertToPage(projectInfoRepository.findAll(isDelete, null, null,status,null), pageable);
        }else {
            return Common.listConvertToPage(projectInfoRepository.findAllByBusiJbpmFlow(isDelete, businessType, parentActor,status), pageable);
        }

    }

    public ProjectCollectionDto to(Page<ProjectInfo> source, HttpServletRequest request) {
        ProjectCollectionDto projectCollectionDto = new ProjectCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, projectCollectionDto, request);
        ProjectInfoDto projectInfoDto;
        for (ProjectInfo projectInfo : source) {
            CargoInfo cargoInfo = cargoInfoRepository.findAllByProjectId(projectInfo.getProjectId());
            InquiryInfoNew inquiryInfoNew = inquiryInfoNewRepository.findAllByInquiryId(projectInfo.getInquiryId());
            cargoInfo.setPartInfos(null);//项目列表用不到配件列表
            projectInfoDto = new ProjectInfoDto();
            if(null!=inquiryInfoNew){
                inquiryInfoNew.getCargoInfo().setPartInfos(null);
                projectInfoDto.setInquiryInfo(inquiryInfoNew);
            }
            BeanUtils.copyProperties(projectInfo, projectInfoDto);
            projectInfoDto.setCargoId(cargoInfo.getCargoId().toString());//货物id
            projectInfoDto.setCargoName(cargoInfo.getCargoName());//货物名称
            projectInfoDto.setCurrency(cargoInfo.getCurrency());//币种
            projectInfoDto.setGuaranteeRate(cargoInfo.getGuaranteeRate());//维保率
            projectInfoDto.setCargoTotal(projectInfo.getCargoTotal());//货物总金额
            projectCollectionDto.add(projectInfoDto);
        }
        return projectCollectionDto;
    }

    public ProjectInfo update(ProjectInfoDto projectInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        String username = jwtUser.getUsername();
        ProjectInfo projectInfo = this.findOne(projectInfoDto.getProjectId());
        CargoInfo cargoInfo = cargoInfoRepository.findAllByCargoId(projectInfoDto.getCargoId());//货物
        ProjectInfo model=new ProjectInfo();
        model.setProjectId(projectInfo.getProjectId());
        model.setCreator(projectInfo.getCreator());
        model.setPurchaser(projectInfoDto.getPurchaser());
        model.setProjectCode(projectInfo.getProjectCode());
        model.setProjectSubject(projectInfoDto.getCargoName()+"的采购");
        model.setDeliveryDate(projectInfoDto.getDeliveryDate());
        model.setDeliveryDateStatus(projectInfoDto.getDeliveryDateStatus());
        model.setPaymentMethod(projectInfoDto.getPaymentMethod());
        model.setPriceTerm(projectInfoDto.getPriceTerm());
        model.setGuaranteeDate(projectInfoDto.getGuaranteeDate());
        model.setGuaranteeFee(projectInfoDto.getGuaranteeFee());
        model.setStatus(projectInfoDto.getStatus());
        model.setCargoTotal(projectInfoDto.getCargoTotal());
        model.setAmount(projectInfoDto.getAmount().replaceAll(",",""));//项目总金额（原来的币种）
        model.setAmountRmb(projectInfoDto.getAmountRmb().replaceAll(",",""));//项目总金额（人民币）
        model.setCurrency(projectInfoDto.getCurrency());
        model.setIsDelete(projectInfo.getIsDelete());
        model.setQuantity(projectInfoDto.getQuantity());//数量
        if(null!=projectInfoDto.getInquiryId()){
            model.setInquiryId(projectInfoDto.getInquiryId());//询价
        }
        model.setAttachment_p(projectInfo.getAttachment_p());
        model.setAttachment_c(projectInfo.getAttachment_c());
        model.setAttachment_n(projectInfo.getAttachment_n());
        projectInfoRepository.update(model.getProjectId(),model.getProjectSubject(),model.getPurchaser(),
                model.getCurrency(),model.getDeliveryDate(),model.getDeliveryDateStatus(),model.getGuaranteeDate(),model.getGuaranteeFee(),
                model.getPaymentMethod(),model.getPriceTerm(),model.getCargoTotal(),model.getAmount(),model.getStatus(),
                 projectInfoDto.getInquiryInfo().getInquiryId(),model.getCreator(),model.getProjectCode(),model.getIsDelete(),model.getQuantity(),model.getAmountRmb());

        //供应商
        AgentInfoExp agentInfoExp=projectInfoDto.getAgentInfoExp();
        List<AgentInfoExp> list=agentInfoExpRepository.findByProjectId(projectInfoDto.getProjectId());
        if(list.get(0).getAgentId().equals(agentInfoExp.getAgentId())){
            //修改推荐代理商的id和数据库存在的推荐代理商id相等（修改推荐代理商信息）
            AgentInfoExp agentInfoExp1=new AgentInfoExp();
            if(agentInfoExp.getAgentId()!=null) {
                Optional<AgentInfoExp> optional=agentInfoExpRepository.findById(agentInfoExp.getAgentId());
                if (optional.isPresent()){
                    agentInfoExp1=optional.get();
                    agentInfoExp.setRemark(agentInfoExp1.getRemark());
                    agentInfoExp.setReviewStatus(agentInfoExp1.getReviewStatus());
                    agentInfoExp.setIsDelete(Constant.DELETE_NO);
                    agentInfoExp.setCreateDate(new Date());
                    agentInfoExp.setCreator(agentInfoExp1.getCreator());
                    agentInfoExp.setAttachment(agentInfoExp1.getAttachment());
                    agentInfoExp.setProjectInfo(model);
                    agentInfoExpRepository.save(agentInfoExp);
                }
            }
        }else {
            //修改推荐代理商的id和数据库存在的推荐代理商id不相等（新增推荐代理商信息）
            agentInfoExpRepository.deleteByProjectId(projectInfoDto.getProjectId());
            agentInfoExp.setOldAgentId(agentInfoExp.getAgentId());
            agentInfoExp.setIsDelete(Constant.DELETE_NO);
            agentInfoExp.setCreateDate(new Date());
            agentInfoExp.setCreator(username);
            agentInfoExp.setAttachment(agentInfoExp.getAttachment());
            agentInfoExp.setProjectInfo(model);
            agentInfoExp.setAgentId(null);
            agentInfoExpRepository.save(agentInfoExp);
        }

        partInfoExpRepository.deleteByProjectId(projectInfoDto.getProjectId());
        //货物配件
        Set<PartInfoExp>partInfoExps=projectInfoDto.getPartInfoExps();
        if (partInfoExps != null && !partInfoExps.isEmpty()) {
            for (PartInfoExp partInfoExp : partInfoExps) {
                partInfoExp.setIsDelete(Constant.DELETE_NO);
                partInfoExp.setProjectInfo(model);
                partInfoExp.setCargoInfo(cargoInfo);
//                partInfoExpRepository.save(partInfoExp);
                partInfoExpRepository.savePartInfoExp(partInfoExp.getIsDelete()
                        ,partInfoExp.getManufactor()
                        ,partInfoExp.getPartCode()
                        ,partInfoExp.getPartName()
                        ,partInfoExp.getPartSerial()
                        ,partInfoExp.getPrice()
                        ,partInfoExp.getQuantity()
                        ,partInfoExp.getRemark()==null?"":partInfoExp.getRemark()
                        ,partInfoExp.getStandards()
                        ,partInfoExp.getTechParams()
                        ,partInfoExp.getTotal()
                        ,partInfoExp.getUnit()
                        ,model.getProjectId()
                        ,cargoInfo.getCargoId());
            }
        }
        return model;
    }

    public ProjectInfoDto findOne(Long projectId) {
        Optional<ProjectInfo> optional = projectInfoRepository.findById(projectId);
        ProjectInfoDto projectInfoDto=new ProjectInfoDto();
        ProjectInfo projectInfo=new ProjectInfo();
        if (optional.isPresent()) {
             projectInfo=optional.get();
            BeanUtils.copyProperties(projectInfo, projectInfoDto);
        }
        List<AgentInfoExp> agentInfoExp=agentInfoExpRepository.findByProjectId(projectId);
        CargoInfo cargoInfo=cargoInfoRepository.findAllByProjectId(projectId);
        ProjectInfo projectInfo1=projectInfoRepository.getOne(projectId);
        InquiryInfoNew inquiryInfoNew=inquiryInfoNewRepository.findAllByInquiryId(projectInfo1.getInquiryId());
        if(agentInfoExp.get(0).getAgentId() !=null){
            AgentInfoExp model=agentInfoExp.get(0);
            AgentInfoExpDto agentInfoExpDto =new AgentInfoExpDto();
            BeanUtils.copyProperties(model,agentInfoExpDto);
            User user=userRepository.findUserInfoByUserName(agentInfoExp.get(0).getAgentName());
            agentInfoExpDto.setCompanyNo(user.getCompany()+"("+user.getUsername()+")");//公司名称+账号
           // projectInfoDto.setAgentInfoExp(model);
            projectInfoDto.setAgentInfoExpDto(agentInfoExpDto);
        }else {
           // projectInfoDto.setAgentInfoExp(null);
            projectInfoDto.setAgentInfoExpDto(null);
        }
        if(null!=inquiryInfoNew){
            inquiryInfoNew.getCargoInfo().setPartInfos(null);
            projectInfoDto.setInquiryInfo(inquiryInfoNew);
            //根据询价id查询询价信息
            projectInfoDto.setProjectEntrustingUnit(inquiryInfoNew.getProjectEntrustingUnit());//项目委托单位
            projectInfoDto.setFinalUser(inquiryInfoNew.getFinalUser());//最终使用单位
            projectInfoDto.setContact(inquiryInfoNew.getContact());//联系人
            projectInfoDto.setContactPhone(inquiryInfoNew.getContactPhone());//联系人电话
            projectInfoDto.setFundsCardNumber(inquiryInfoNew.getFundsCardNumber());//经费卡号
        }else {
            projectInfoDto.setInquiryInfo(null);
        }
        projectInfoDto.setCargoId(cargoInfo.getCargoId().toString());
        projectInfoDto.setCargoName(cargoInfo.getCargoName());
        return projectInfoDto;
    }

    public void delete(List<Long> projectIds) {
        projectInfoRepository.updateIsDelete(projectIds);
    }

    public void export(HttpServletResponse response, List<Long> projectIds,String actor) {
        try {
            String[] header = {"项目主题", "项目编号", "货物名称", "货物金额", "项目总金额", "币种","项目总金额RMB", "状态",
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
            } else if(null!=actor && projectIds.size()==0 ){
                list = projectInfoRepository.findAlltoExpert(Constant.DELETE_NO,actor);
            }else {
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
                    row.createCell(8).setCellValue(new HSSFRichTextString(attachmentp.getAttachName()));
                } else {
                    row.createCell(8).setCellValue(new HSSFRichTextString(""));
                }
                if (attachmentn != null) {
                    attachmentn = attachmentRepository.getOne(projectInfo.getAttachment_n().getAttachId());
                    row.createCell(9).setCellValue(new HSSFRichTextString(attachmentn.getAttachName()));
                } else {
                    row.createCell(9).setCellValue(new HSSFRichTextString(""));
                }
                if (attachmentc != null) {
                    attachmentc = attachmentRepository.getOne(projectInfo.getAttachment_c().getAttachId());
                    row.createCell(10).setCellValue(new HSSFRichTextString(attachmentc.getAttachName()));
                } else {
                    row.createCell(10).setCellValue(new HSSFRichTextString(""));
                }

                CargoInfo cargoInfo = cargoInfoRepository.findAllByProjectId(projectInfo.getProjectId());
                row.createCell(0).setCellValue(new HSSFRichTextString(projectInfo.getProjectSubject()));
                row.createCell(1).setCellValue(new HSSFRichTextString(projectInfo.getProjectCode()));
                row.createCell(2).setCellValue(new HSSFRichTextString(cargoInfo.getCargoName()));
                row.createCell(3).setCellValue(new HSSFRichTextString(projectInfo.getCargoTotal()+""));//货物金额
                row.createCell(4).setCellValue(new HSSFRichTextString(projectInfo.getAmount()+""));//项目总金额
                row.createCell(5).setCellValue(new HSSFRichTextString(cargoInfo.getCurrency()));//币种
                row.createCell(6).setCellValue(new HSSFRichTextString(projectInfo.getAmountRmb()+""));//项目总金额RMB
                row.createCell(7).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(projectInfo.getStatus())));//状态
            }
            response.setHeader("Content-disposition", "attachment;fileName=projectInfo.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error("项目导出出现异常",e);
        }
    }

}
