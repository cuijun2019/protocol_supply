package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.ResultNoticeCollectionDto;
import com.etone.protocolsupply.model.dto.notice.ResultNoticeDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.notice.ResultNotice;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.notice.ResultNoticeRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.service.inquiry.InquiryInfoNewService;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.ImageUtil;
import com.etone.protocolsupply.utils.PagingMapper;
import com.etone.protocolsupply.utils.WordToPDFUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ResultNoticeService {

    private static final Logger logger = LoggerFactory.getLogger(ResultNoticeService.class);

    @Autowired
    private ResultNoticeRepository resultNoticeRepository;

    @Autowired
    private PagingMapper pagingMapper;

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Value("${file.upload.path.filePath}")
    protected String uploadFilePath;

    @Autowired
    private WordToPDFUtil wordToPDFUtil;

    @Autowired
    private InquiryInfoNewService inquiryInfoNewService;


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

    public Page<ResultNotice> findMyResultNotices(String projectCode, String projectSubject, JwtUser user, Pageable pageable) {

        String username = user.getUsername();
        //判断当前用户是什么角色，如果是招标中心经办人或者招标科长或者admin则查询全部结果通知书
        Long roleId = userRepository.findRoleIdByUsername(username);
        if( "5".equals(roleId+"") || "6".equals(roleId+"")|| "7".equals(roleId+"")){
            List<ResultNotice> list=resultNoticeRepository.findMyResultNoticesAll(projectCode, projectSubject);
            return Common.listConvertToPage(list, pageable);
        }else {
            return Common.listConvertToPage(resultNoticeRepository.findMyResultNotices(projectCode, projectSubject,username), pageable);
        }

    }


    public Page<ResultNotice> findContractNotice(Specification<ResultNotice> specification, Pageable pageable) {
        return resultNoticeRepository.findAll(specification, pageable);
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

    public ResultNotice update(String projectId) {
        ResultNotice model= resultNoticeRepository.findInfoByProjectId(projectId);
        ResultNotice resultNotice = new ResultNotice();
        if (null!=model) {
            resultNotice = model;
            resultNotice.setStatus(Constant.STATE_SIGNED); //待签收-->已签收
            resultNotice.setSignDate(new Date());
            resultNoticeRepository.save(resultNotice);
        }
        return resultNotice;
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
            }else {
                list = resultNoticeRepository.findAll();
            }
            ResultNotice resultNotice;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                resultNotice = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(resultNotice.getProjectSubject()));
                row.createCell(1).setCellValue(new HSSFRichTextString(resultNotice.getProjectCode()));
                row.createCell(2).setCellValue(new HSSFRichTextString(resultNotice.getSupplier() == null ? "" : resultNotice.getSupplier()));
                row.createCell(3).setCellValue(new HSSFRichTextString(resultNotice.getAmount()));
                row.createCell(4).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(resultNotice.getStatus())));
                row.createCell(5).setCellValue(new HSSFRichTextString(resultNotice.getPurchaser()));
                row.createCell(6).setCellValue(new HSSFRichTextString(resultNotice.getCreator()));
                row.createCell(7).setCellValue(new HSSFRichTextString(format.format(resultNotice.getCreateDate())));
                String signDate;
                if (resultNotice.getSignDate() == null) {
                    signDate = "";
                } else {
                    signDate = format.format(resultNotice.getSignDate());
                }
                row.createCell(8).setCellValue(new HSSFRichTextString(signDate));
            }
            response.setHeader("Content-disposition", "attachment;fileName=resultNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            logger.error("导出采购结果通知书异常",e);
        }
    }

    public ProjectInfo getRelateProject(String resultNoticeId) {
        return resultNoticeRepository.findById(Long.parseLong(resultNoticeId)).get().getProjectInfo();
    }

    public ResultNotice save(String projectId, JwtUser user) {
        Attachment attachment = new Attachment();

        Optional<ProjectInfo> optional = projectInfoRepository.findById(Long.valueOf(projectId));
        ProjectInfoDto projectInfoDto=new ProjectInfoDto();
        ProjectInfo projectInfo=new ProjectInfo();
        if (optional.isPresent()) {
            projectInfo=optional.get();
            BeanUtils.copyProperties(projectInfo, projectInfoDto);
        }
        try{

            //查询创建人所在公司
            User creator = userRepository.findByUsername(projectInfo.getCreator());

            //查询采购人所在的学院单位
            InquiryInfoNew inquiryInfoNew = inquiryInfoNewService.findOne(projectInfo.getInquiryId());
            String finalUser = inquiryInfoNew.getFinalUser();


            //无水印uuid图片
            String uuid = UUID.randomUUID().toString().substring(0,8);

            //加水印uuid图片
            String uuid_icon = UUID.randomUUID().toString().substring(0,8);

            String imageType="采购结果通知书";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String path = uploadFilePath + Common.getYYYYMMDate(new Date());

            //生成采购结果通知书
            ImageUtil.getImage(projectInfo,creator,imageType,path,path+"/"+imageType+"_"+sdf.format(new Date())+uuid+".png","", finalUser);

            //图片盖章
            ImageUtil.markImageByIcon(uploadFilePath+"timg1.png",path+"/"+imageType+"_"+sdf.format(new Date())+uuid+".png",path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".jpg",null,imageType);

            //转成PDF文件
            wordToPDFUtil.convert(path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".jpg",path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".pdf");

            //附件表增加记录
            attachment.setAttachName(imageType+"_"+sdf.format(new Date())+uuid_icon+".pdf");
            attachment.setFileType("application/pdf");
            attachment.setPath(path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".pdf");
            attachment.setUploadTime(new Date());
            attachment.setUploader(user.getUsername());
            attachment.setProjectCode(projectInfo.getProjectCode());
            attachment = attachmentRepository.save(attachment);

        }catch (Exception e){
            logger.error("生成采购结果通知书图片异常",e);
        }
        Long proId = Long.valueOf(projectId);
        ResultNotice resultNotice = new ResultNotice();
        resultNotice.setProjectCode(projectInfo.getProjectCode());
        resultNotice.setProjectSubject(projectInfo.getProjectSubject());
        resultNotice.setAmount(projectInfo.getAmountRmb()+"");
        resultNotice.setSupplier(projectInfoRepository.getAgentName(proId));
        resultNotice.setStatus(Constant.STATE_WAIT_SIGN);
        resultNotice.setCreator(user.getFullname());
        resultNotice.setCreateDate(new Date());
        resultNotice.setPurchaser(projectInfo.getPurchaser());
        resultNotice.setProjectInfo(projectInfo);
        resultNotice.setAttachment(attachment);
        resultNoticeRepository.save(resultNotice);

        projectInfoRepository.updatePurchaseId(attachment.getAttachId(),Long.parseLong(projectId));

        return resultNotice;
    }
}
