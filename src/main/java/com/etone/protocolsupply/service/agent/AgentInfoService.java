package com.etone.protocolsupply.service.agent;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.AgentExpCollectionDto;
import com.etone.protocolsupply.model.dto.AgentInfoExpDto;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.agent.AgentCollectionDto;
import com.etone.protocolsupply.model.dto.agent.AgentInfoDto;
import com.etone.protocolsupply.model.dto.partner.PartnerInfoDtoUsername;
import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import com.etone.protocolsupply.model.entity.supplier.BankInfo;
import com.etone.protocolsupply.model.entity.supplier.CertificateInfo;
import com.etone.protocolsupply.model.entity.supplier.ContactInfo;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AgentInfoRepository;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.project.AgentInfoExpRepository;
import com.etone.protocolsupply.repository.supplier.BankInfoRepository;
import com.etone.protocolsupply.repository.supplier.CertificateInfoRepository;
import com.etone.protocolsupply.repository.supplier.ContactInfoRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import com.etone.protocolsupply.repository.user.RoleRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.utils.BcryptCipher;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class AgentInfoService {

    @Autowired
    private AgentInfoRepository    agentInfoRepository;

    @Autowired
    private AgentInfoExpRepository agentInfoExpRepository;

    @Autowired
    private AttachmentRepository   attachmentRepository;

    @Autowired
    private PagingMapper           pagingMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PartnerInfoRepository partnerInfoRepository;

    @Autowired
    private BankInfoRepository bankInfoRepository;

    @Autowired
    private CertificateInfoRepository certificateInfoRepository;

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Autowired
    private RoleRepository roleRepository;

    public String save(Map<String,String> registerData){
        //先根据用户名查询是否已存在该用户
        User check = userRepository.findByUsername(registerData.get("creditCode"));
        if(check!=null){
            return "该用户已经存在";
        }

        //新增代理商记录
        PartnerInfo partnerInfo = new PartnerInfo();
        partnerInfo.setSupType(Integer.parseInt(registerData.get("supType")));//交易主题一级
        try {
            partnerInfo.setSupTypeChild(Integer.parseInt(registerData.get("supTypeChild")));//交易主体二级
        } catch (Exception e) {
            e.printStackTrace();
        }
        partnerInfo.setCompanyNo(registerData.get("company"));
        partnerInfo.setAuthStatus(1);//认证状态 1已认证   2未认证
        partnerInfo.setAuthMethod("普通认证");
        partnerInfo.setAuthDate(new Date());
        partnerInfo.setIsDelete(2);
        partnerInfo.setRegisterTime(new Date());
        partnerInfo.setIsAuditing(1);//1 草稿 2 审核中 3同意 4退回 5 完成
        partnerInfo = partnerInfoRepository.save(partnerInfo);

        //新增关联银行账户信息
        BankInfo bankInfo = new BankInfo();
        bankInfo.setPartnerId(partnerInfo.getPartnerId());
        bankInfoRepository.save(bankInfo);

        //新增关联三证信息
        CertificateInfo certificateInfo = new CertificateInfo();
        certificateInfo.setCreditCode(registerData.get("creditCode"));
        certificateInfo.setIsCertificate(1);//是否三证合一
        certificateInfo.setModifyStatus(2);
        certificateInfo.setPartnerId(partnerInfo.getPartnerId());
        certificateInfoRepository.save(certificateInfo);

        //新增关联联系人信息
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setFullname(registerData.get("realName"));
        contactInfo.setEmail(registerData.get("email"));
        contactInfo.setTelephone(registerData.get("telephone"));
        contactInfo.setPartnerId(partnerInfo.getPartnerId());
        contactInfoRepository.save(contactInfo);


        //新增用户
        User user = new User();
        user.setCreateTime(new Date());
        user.setEnabled(true);
        user.setIsDelete(2);
        user.setSex("");
        user.setUpdateTime(new Date());
        user.setUsername(registerData.get("creditCode"));
        user.setCompany(registerData.get("company"));
        user.setPassword(BcryptCipher.Bcrypt(registerData.get("passwordChecked")).get("cipher"));
        user.setFullname(registerData.get("realName"));
        user.setTelephone(registerData.get("telephone"));
        user.setEmail(registerData.get("email"));
        user.setPartnerInfo(partnerInfo);
        user = userRepository.save(user);

        //分配角色,交易主体类型（1：供应商；2：代理商）
        if("1".equals(registerData.get("supType")+"")){
            roleRepository.addUserRole(user.getId(),Long.parseLong("1"));
        }else {
            roleRepository.addUserRole(user.getId(),Long.parseLong("2"));
        }
        return "注册成功";
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

    public Page<AgentInfo> findAgentInfos(String agentName, String status, String isDelete, JwtUser actor, Pageable pageable, String reviewStatus) {
        //判断当前用户是什么角色，如果是招标中心经办人或者招标科长或者admin则查询全部代理商
        Long roleId = userRepository.findRoleIdByUsername(actor.getUsername());
        if( "5".equals(roleId+"") || "6".equals(roleId+"")|| "7".equals(roleId+"")){
                return Common.listConvertToPage(agentInfoRepository.findAll(agentName, status, isDelete,reviewStatus), pageable);
            }else {
                return Common.listConvertToPage(agentInfoRepository.findMyAgent(agentName, status, isDelete,actor.getUsername(),reviewStatus), pageable);
            }
    }

    public Page<AgentInfo> getAgentList( String status, String isDelete, String actor, Pageable pageable, String reviewStatus) {
        return Common.listConvertToPage(agentInfoRepository.getAgentList(status,actor, isDelete,reviewStatus), pageable);

    }
    public Page<AgentInfo> findAgents(Specification<AgentInfo> specification, Pageable pageable) {
        return agentInfoRepository.findAll(specification, pageable);
    }

    //项目-代理商列表
    public Page<AgentInfoExp> findAgentExps(String projectId, String isDelete, Pageable pageable) {
        return Common.listConvertToPage(agentInfoExpRepository.findAll(projectId, isDelete), pageable);
    }

    public AgentCollectionDto to(Page<AgentInfo> source, HttpServletRequest request) {
        AgentCollectionDto agentCollectionDto = new AgentCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, agentCollectionDto, request);
        AgentInfoDto agentInfoDto;
        for (AgentInfo agentInfo : source) {
            agentInfoDto = new AgentInfoDto();
            BeanUtils.copyProperties(agentInfo, agentInfoDto);
            //User user= userRepository.findByPartnerId(agentInfo.getPartnerId());
            PartnerInfo partnerInfo=partnerInfoRepository.getOne(agentInfo.getPartnerId());
            agentInfoDto.setAgentName(partnerInfo.getCompanyNo()==null?"":partnerInfo.getCompanyNo());
            agentCollectionDto.add(agentInfoDto);
        }
        return agentCollectionDto;
    }

    public AgentCollectionDto getAgentListTo(Page<AgentInfo> source, HttpServletRequest request,String projectId,String actor) {
        User user=userRepository.findByUsername(actor);
        AgentCollectionDto agentCollectionDto = new AgentCollectionDto();
        Long roleId = userRepository.findRoleIdByUsername(actor);
        if("2".equals(roleId+"") || "1".equals(roleId+"")){
            AgentInfoDto agentInfoMs = new AgentInfoDto();
            agentInfoMs.setAgentId(user.getId());
            agentInfoMs.setAgentName(user.getUsername());
            agentInfoMs.setAgentPoint("0%");
            agentInfoMs.setCompanyNo(user.getCompany()+"("+user.getUsername()+")");//默认第一个供应商是自己
            agentCollectionDto.add(agentInfoMs);
        }
        pagingMapper.storeMappedInstanceBefore(source, agentCollectionDto, request);
        AgentInfoDto agentInfoDto;
        String type="0";
        if(null!=projectId && !"".equals(projectId)){//修改编辑项目
            List<AgentInfoExp> agentInfoExp=agentInfoExpRepository.findByProjectId(Long.parseLong(projectId));
            for (AgentInfo agentInfo : source) {
                type="1";
                agentInfoDto = new AgentInfoDto();
                User user1= userRepository.findByPartnerId(agentInfo.getPartnerId());
                if(actor.equals(agentInfo.getAgentName())){
                    agentInfoDto.setCompanyNo(user.getCompany()+"("+user.getUsername()+")");
                }else {
                    agentInfoDto.setCompanyNo(user1.getCompany()==null?"":user1.getCompany()+"("+user1.getUsername()+")");
                }

                if(null==agentInfoExp.get(0).getOldAgentId() || !agentInfoExp.get(0).getOldAgentId().equals(agentInfo.getAgentId())){
                    BeanUtils.copyProperties(agentInfo, agentInfoDto);
                }else if(agentInfoExp.get(0).getOldAgentId().equals(agentInfo.getAgentId())){
                    //如果代理商拓展表存在该代理商信息，替换原来的代理商信息，显示新建项目所推荐的代理商信息（备注字段）
                    BeanUtils.copyProperties(agentInfoExp.get(0), agentInfoDto);
                    agentInfoDto.setRemark(agentInfoExp.get(0).getRemark());
                }
                agentCollectionDto.add(agentInfoDto);
            }
            if(type.equals("0")){
                AgentInfoDto agentInfoDto1 =new AgentInfoDto();
                User user2= userRepository.findByUsername(agentInfoExp.get(0).getAgentName());
                BeanUtils.copyProperties(agentInfoExp.get(0), agentInfoDto1);
                agentInfoDto1.setCompanyNo(user2.getCompany()+"("+user2.getUsername()+")");
                agentCollectionDto.add(agentInfoDto1);
            }
            type="0";

        }else {//新建项目
            for (AgentInfo agentInfo : source) {
                agentInfoDto = new AgentInfoDto();
                User user1= userRepository.findByPartnerId(agentInfo.getPartnerId());
                if(actor.equals(agentInfo.getAgentName())){
                    agentInfoDto.setCompanyNo(user.getCompany()+"("+user.getUsername()+")");
                }else {
                    agentInfoDto.setCompanyNo(user1.getCompany()==null?"":user1.getCompany()+"("+user1.getUsername()+")");
                }
                    BeanUtils.copyProperties(agentInfo, agentInfoDto);
                    agentCollectionDto.add(agentInfoDto);
            }
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

    public AgentInfo update(AgentInfoDto agentInfoDto)  {
        agentInfoRepository.updateStatus(agentInfoDto.getStatus(), agentInfoDto.getReviewStatus(), agentInfoDto.getAgentId());

        return agentInfoRepository.findById(agentInfoDto.getAgentId()).get();
    }

    public void delete(List<Long> agentIds) {
        agentInfoRepository.updateIsDelete(agentIds);
    }

    public void export(HttpServletResponse response, String agentName, String status, String isDelete, List<Long> agentIds,JwtUser actor) {
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
            } else if(null!=actor.getUsername() && agentIds.isEmpty()) {
                list = agentInfoRepository.findExpert(Constant.DELETE_NO,actor.getUsername() );
            }else {
                list = agentInfoRepository.findAll();
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
            response.setHeader("Content-disposition", "attachment;fileName=agentInfo.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PartnerInfoDtoUsername> findAgentsList(String agentName) {
        //是否有搜索条件
        List<PartnerInfoDtoUsername> partnerInfoList = new ArrayList<>();
        List<Map<String,Object>> partnerInfoListObj = new ArrayList<>();
        if (agentName != null && !"".equals(agentName)){
             partnerInfoListObj = partnerInfoRepository.findVerifiedSuppliersByagentName(agentName);
        }else {
             partnerInfoListObj = partnerInfoRepository.findVerifiedSuppliers();
        }
        if(partnerInfoListObj!=null && partnerInfoListObj.size()>0){
            for (int i = 0; i < partnerInfoListObj.size(); i++) {
                PartnerInfoDtoUsername partnerInfoDto = new PartnerInfoDtoUsername();
                String code = partnerInfoListObj.get(i).get("username") == null ? null : partnerInfoListObj.get(i).get("username").toString();
                String companyName = partnerInfoListObj.get(i).get("company_no") == null ? null : partnerInfoListObj.get(i).get("company_no").toString();
                partnerInfoDto.setUsername(code);
//                partnerInfoDto.setCompanyNo(companyName+"("+code+")");
                partnerInfoDto.setCompanyNo(companyName);
                partnerInfoDto.setPartnerId(Long.parseLong(partnerInfoListObj.get(i).get("partner_id")+""));
                partnerInfoList.add(partnerInfoDto);
            }
        }
        return partnerInfoList;
    }

    public AgentInfo saveAgent(AgentInfoDto agentInfo, JwtUser user) {
        AgentInfo info = new AgentInfo();
        info.setAgentName(agentInfo.getAgentName());//代理商名称
        info.setAgentPoint(agentInfo.getAgentPoint());//代理费扣点（百分比）
        info.setStatus(agentInfo.getStatus());//激活状态
        info.setReviewStatus(agentInfo.getReviewStatus());//审核状态
        info.setCreator(user.getUsername());//创建者
        info.setCreateDate(new Date());//创建时间
        info.setIsDelete(2);//是否删除
        info.setValidDateStart(agentInfo.getValidDateStart());//有效日期开始
        info.setValidDateEnd(agentInfo.getValidDateEnd());//有效日期结束
        info.setAttachment(agentInfo.getAttachment());//附件
        info.setPartnerId(agentInfo.getPartnerId());//供应商id
        return agentInfoRepository.save(info);
    }
}
