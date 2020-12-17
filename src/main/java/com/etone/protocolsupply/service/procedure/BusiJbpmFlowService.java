package com.etone.protocolsupply.service.procedure;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.repository.user.RoleRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.service.cargo.PartInfoService;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class BusiJbpmFlowService {
    private static final Logger logger = LoggerFactory.getLogger(BusiJbpmFlowService.class);
    @Autowired
    private BusiJbpmFlowRepository busiJbpmFlowRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private PagingMapper         pagingMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    //待办新增
    public BusiJbpmFlow save(BusiJbpmFlowDto busiJbpmFlowDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        BusiJbpmFlow busiJbpmFlow = new BusiJbpmFlow();
        BeanUtils.copyProperties(busiJbpmFlowDto, busiJbpmFlow);
        if(busiJbpmFlowDto.getInquiryCode()!=null){
            busiJbpmFlow.setInquiryCode(busiJbpmFlowDto.getInquiryCode());
        }
        busiJbpmFlow.setFlowStartTime(date);//询价时间
        busiJbpmFlow.setFlowInitorId(userName);
        busiJbpmFlow.setType(Constant.BUSINESS_TYPE_DAIBAN);

//        Attachment attachment=busiJbpmFlowDto.getAttachment_feasibility();
//        if (attachment != null && attachment.getAttachId() != null && !attachment.getAttachId().equals("")) {
//            projectInfoRepository.updateFeasibility_fileId(attachment.getAttachId(),busiJbpmFlowDto.getBusinessId());
//        }
        busiJbpmFlow = busiJbpmFlowRepository.save(busiJbpmFlow);
        return busiJbpmFlow;
    }
    //已办新增
    public BusiJbpmFlow saveBusiApproveResult(BusiJbpmFlowDto busiJbpmFlowDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        BusiJbpmFlow busiJbpmFlow = new BusiJbpmFlow();
        BeanUtils.copyProperties(busiJbpmFlowDto, busiJbpmFlow);
        busiJbpmFlow.setFlowStartTime(date);//询价时间
        busiJbpmFlow.setFlowInitorId(userName);
        busiJbpmFlow.setType(Constant.BUSINESS_TYPE_YIBAN);
        busiJbpmFlow = busiJbpmFlowRepository.save(busiJbpmFlow);
        return busiJbpmFlow;
    }
    //待阅新增
    public BusiJbpmFlow saveToBeRead(BusiJbpmFlowDto busiJbpmFlowDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        BusiJbpmFlow busiJbpmFlow = new BusiJbpmFlow();
        BeanUtils.copyProperties(busiJbpmFlowDto, busiJbpmFlow);
        busiJbpmFlow.setFlowStartTime(date);//询价时间
        busiJbpmFlow.setFlowInitorId(userName);
        //busiJbpmFlow.setNextActor(leader);
        busiJbpmFlow.setTaskState("0");
        busiJbpmFlow.setReadType(Constant.BUSINESS_TYPE_DAIYUE);//待阅
        busiJbpmFlow = busiJbpmFlowRepository.save(busiJbpmFlow);
        return busiJbpmFlow;
    }
    //已阅新增
    public BusiJbpmFlow saveAlreadyRead(BusiJbpmFlowDto busiJbpmFlowDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        BusiJbpmFlow busiJbpmFlow = new BusiJbpmFlow();
        BeanUtils.copyProperties(busiJbpmFlowDto, busiJbpmFlow);
        busiJbpmFlow.setFlowStartTime(date);
        busiJbpmFlow.setFlowInitorId(userName);
        busiJbpmFlow.setType(Constant.BUSINESS_TYPE_YIYUE);
        busiJbpmFlow = busiJbpmFlowRepository.save(busiJbpmFlow);
        return busiJbpmFlow;
    }
    public BusiJbpmFlow updateAlreadyRead(BusiJbpmFlowDto busiJbpmFlowDto)throws GlobalServiceException{
        busiJbpmFlowRepository.updateIsReadType(busiJbpmFlowDto.getId(),busiJbpmFlowDto.getBusinessSubject());
        Optional<BusiJbpmFlow> optional=busiJbpmFlowRepository.findById(busiJbpmFlowDto.getId());
        BusiJbpmFlow busiJbpmFlow=new BusiJbpmFlow();
        if (optional.isPresent()) {
             busiJbpmFlow=optional.get();
        }
        return busiJbpmFlow;
    }



    public Specification<BusiJbpmFlow> getWhereClause(String businessType, String businessSubject,Integer type,Integer readType, String businessId,String parentActor,String nextActor) {
        return (Specification<BusiJbpmFlow>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(businessType)) {
                predicates.add(criteriaBuilder.equal(root.get("businessType").as(String.class), businessType));
            }
            if (Strings.isNotBlank(businessSubject)) {
                predicates.add(criteriaBuilder.like(root.get("businessSubject").as(String.class), '%'+businessSubject+'%'));
            }
            if (Strings.isNotBlank(businessId)) {
                predicates.add(criteriaBuilder.equal(root.get("businessId").as(String.class), businessId));
            }
            if (Strings.isNotBlank(parentActor)) {
                predicates.add(criteriaBuilder.equal(root.get("parentActor").as(String.class), parentActor));
            }
            if (Strings.isNotBlank(nextActor)) {
                predicates.add(criteriaBuilder.equal(root.get("nextActor").as(String.class), nextActor));
            }
            if (type!=null) {
                predicates.add(criteriaBuilder.equal(root.get("type").as(Integer.class), type));
            }
            if (readType!=null) {
                predicates.add(criteriaBuilder.equal(root.get("readType").as(Integer.class), readType));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<BusiJbpmFlow> findBusiJF(String businessType, String businessSubject,Integer type,Integer readType,
                                         String businessId,String parentActor,String nextActor,String timeOrder, Pageable pageable) {
        Integer action =null;
        if( null!=type && type==0 ){
            //待办节点没有草稿状态
            action=1;
        }
        if(timeOrder.equals("ASC")){
            return Common.listConvertToPage(busiJbpmFlowRepository.findAllListAsc(businessType, businessSubject, type,readType,
                    businessId,parentActor,nextActor,action), pageable);

        }else {
            return Common.listConvertToPage(busiJbpmFlowRepository.findAllList(businessType, businessSubject, type,readType,
                    businessId,parentActor,nextActor,action), pageable);
        }

    }

    public BusiJbpmFlowCollectionDto to(Page<BusiJbpmFlow> source, HttpServletRequest request,JwtUser jwtUser) {
        BusiJbpmFlowCollectionDto busiJbpmFlowCollectionDto = new BusiJbpmFlowCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, busiJbpmFlowCollectionDto, request);
        BusiJbpmFlowDto busiJbpmFlowDto;
        for (BusiJbpmFlow busiJbpmFlow : source) {
            busiJbpmFlowDto = new BusiJbpmFlowDto();
            BeanUtils.copyProperties(busiJbpmFlow, busiJbpmFlowDto);
            List<Role> list=roleRepository.findRoleByNextActor(busiJbpmFlow.getNextActor());
            if(!list.isEmpty()){
                busiJbpmFlowDto.setNextActor_roleId(list.get(0).getId());
                busiJbpmFlowDto.setNextActor_roleName(list.get(0).getName());
                busiJbpmFlowDto.setNextActor_roleDescription(list.get(0).getDescription());
                busiJbpmFlowDto.setNextActor_roleStatus(list.get(0).getStatus());
            }
            User user=userRepository.findByUsername(busiJbpmFlow.getFlowInitorId());
            busiJbpmFlowDto.setCompanyName(user.getCompany()+"("+user.getFullname()+")");//待办创建人显示公司+名称
            Attachment attachment=new Attachment();
            if(busiJbpmFlow.getAttachment()!=null){
                Optional<Attachment> optional=attachmentRepository.findById(busiJbpmFlow.getAttachment().getAttachId());
                if (optional.isPresent()) {
                    attachment=optional.get();
                }
            }
            busiJbpmFlowDto.setAttachment(attachment);
            busiJbpmFlowCollectionDto.add(busiJbpmFlowDto);
        }
        return busiJbpmFlowCollectionDto;
    }

    public void export(HttpServletResponse response, List<Long> ids,Integer type,Integer readType,String nextActor) {
        try {
            String str1=null;
            if(null!=type){
                if(type==0){
                    str1="待办";
                } else if(type==1) {
                    str1="已办";
                }
            }
            if(null !=readType){
                if(readType==0) {
                    str1="待阅";
                } else if(readType==1) {
                    str1="已阅";
                }
            }

            String[] header = {str1+"类型", str1+"主题", "状态", "当前处理人", "创建人", "创建时间","审核意见","审核结果"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(str1+"信息表");
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
            if (ids != null && ids.size()!=0 ) {
                list = busiJbpmFlowRepository.findAll(ids);
            }else if(ids.isEmpty()){
                list = busiJbpmFlowRepository.findAllToExpert(type,readType,nextActor);
            }else {
                list=busiJbpmFlowRepository.findAll();
            }
            BusiJbpmFlow busiJbpmFlow;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                busiJbpmFlow = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(Constant.BUSINESS_TYPE_STATUS_MAP.get(busiJbpmFlow.getBusinessType())));
                row.createCell(1).setCellValue(new HSSFRichTextString(busiJbpmFlow.getBusinessSubject()));
                row.createCell(2).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(Integer.parseInt(busiJbpmFlow.getTaskState()))));
                row.createCell(3).setCellValue(new HSSFRichTextString(busiJbpmFlow.getParentActor()));
                row.createCell(4).setCellValue(new HSSFRichTextString(busiJbpmFlow.getFlowInitorId()));
                row.createCell(5).setCellValue(new HSSFRichTextString(format.format(busiJbpmFlow.getFlowStartTime())));
                row.createCell(6).setCellValue(new HSSFRichTextString(busiJbpmFlow.getOpinion()));
                row.createCell(7).setCellValue(new HSSFRichTextString(busiJbpmFlow.getAction()));
            }
            if(type!=null){
                if(type==0){
                    response.setHeader("Content-disposition", "attachment;filename=busiJbpmFlow.xls");
                }else if(type==1) {
                    response.setHeader("Content-disposition", "attachment;filename=busiApproveResult.xls");
                }
            }
            if(readType!=null){
                if(readType==0) {
                    response.setHeader("Content-disposition", "attachment;filename=toBeRead.xls");
                }else if(readType==1) {
                    response.setHeader("Content-disposition", "attachment;filename=alreadyRead.xls");
                }
            }
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("审批流程导出出现异常",e);
        }
    }

    //根据业务表id，待办类型，当前处理人 修改type=1
//    public Specification<BusiJbpmFlow> getWhereThreeClause(String businessId, String businessType,String nextActor) {
//        return (Specification<BusiJbpmFlow>) (root, criteriaQuery, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            if (Strings.isNotBlank(businessType)) {
//                predicates.add(criteriaBuilder.equal(root.get("businessType").as(String.class), businessType));
//            }
//            if (Strings.isNotBlank(businessId)) {
//                predicates.add(criteriaBuilder.equal(root.get("businessId").as(String.class), businessId));
//            }
//            if (Strings.isNotBlank(nextActor)) {
//                predicates.add(criteriaBuilder.equal(root.get("nextActor").as(String.class), nextActor));
//            }
//
//            Predicate[] pre = new Predicate[predicates.size()];
//            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
//        };
//    }

    public List<BusiJbpmFlow> getWhereThreeClause(String businessId, String businessType,String nextActor){
        return busiJbpmFlowRepository.isBusiJbpmFlows(businessId,businessType,nextActor);
    }

    public List<BusiJbpmFlow> getBJFListWithReadType(String businessId, String businessType,String nextActor){
        return busiJbpmFlowRepository.findBJFListWithReadType(businessId,businessType,nextActor);
    }

    public List<BusiJbpmFlow> isBusiJbpmFlows(String businessId, String businessType,String parentActor,String nextActor){
        return busiJbpmFlowRepository.isCover(businessId,businessType,parentActor,nextActor);
    }

    public List<BusiJbpmFlow> updateNextActor(String businessId, String businessType,Integer type){
        return busiJbpmFlowRepository.updateNextActor(businessId,businessType,type);
    }


    public List<BusiJbpmFlow> isExistBusiJbpmFlows(String businessId, String businessType,String parentActor,String nextActor, Integer type){
        return busiJbpmFlowRepository.isExistBusiJbpmFlows(businessId,businessType,parentActor,nextActor,type);
    }

    public List<BusiJbpmFlow> getModel(Specification<BusiJbpmFlow> specification){
        return busiJbpmFlowRepository.findAll(specification);
    }

    public void updateType(String businessId, String businessType,String nextActor){
         busiJbpmFlowRepository.updateType(businessId,businessType,nextActor);
    }

    public void updateReadType(Long id){
        busiJbpmFlowRepository.updateReadType(id);
    }

    public void upnextActor(Long id,String nextActor){
            busiJbpmFlowRepository.upNextActor(id,nextActor);
    }



}
