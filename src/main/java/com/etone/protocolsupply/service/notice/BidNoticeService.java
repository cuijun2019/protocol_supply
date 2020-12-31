package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.BidNoticeCollectionDto;
import com.etone.protocolsupply.model.dto.notice.BidNoticeDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.notice.BidNoticeRepository;
import com.etone.protocolsupply.repository.notice.ContractNoticeRepository;
import com.etone.protocolsupply.repository.project.AgentInfoExpRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.service.inquiry.InquiryInfoNewService;
import com.etone.protocolsupply.utils.*;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Transactional(rollbackFor = Exception.class)
@Service
@PropertySource(value = {
        "classpath:myApplication.properties",
}, encoding = "utf-8")
public class BidNoticeService {

    private static final Logger logger = LoggerFactory.getLogger(BidNoticeService.class);

    @Autowired
    private PagingMapper          pagingMapper;

    @Autowired
    private BidNoticeRepository   bidNoticeRepository;

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private AgentInfoExpRepository agentInfoExpRepository;

    @Autowired
    private InquiryInfoNewService inquiryInfoNewService;

    @Value("${file.upload.path.filePath}")
    protected String uploadFilePath;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String host;

    @Value("${email.address}")
    private String email;

    @Autowired
    private WordToPDFUtil wordToPDFUtil;

    public BidNotice save(String projectId, JwtUser jwtUser) {
        Attachment attachment = new Attachment();//未加密附件
        Attachment attachmentEncrypt = new Attachment();//加密附件

        //查询项目详情
        ProjectInfo projectInfo = projectInfoRepository.findAllByProjectId(Long.valueOf(projectId));

        //根据项目id查询代理商名称
        List<AgentInfoExp> agentInfoExp = agentInfoExpRepository.findByProjectId(Long.valueOf(projectId));

        try{

            //查询项目创建人(制造商)所在公司
            User creator = userRepository.findByUsername(projectInfo.getCreator());

            //查询采购人所在的学院单位
            InquiryInfoNew inquiryInfoNew = inquiryInfoNewService.findOne(projectInfo.getInquiryId());
            String finalUser = inquiryInfoNew.getFinalUser();


            //无水印uuid图片
            String uuid = UUID.randomUUID().toString().substring(0,8);
            //加水印uuid图片
            String uuid_icon = UUID.randomUUID().toString().substring(0,8);

            String imageType="成交通知书";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String path = uploadFilePath + Common.getYYYYMMDate(new Date());

            //生成成交通知书图片
            ImageUtil.getImage(projectInfo,creator,imageType,path,path+"/"+imageType+"_"+sdf.format(new Date())+uuid+".png",creator.getCompany(),finalUser);

            //图片盖章
            ImageUtil.markImageByIcon(uploadFilePath+"timg1.png",path+"/"+imageType+"_"+sdf.format(new Date())+uuid+".png",path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".jpg",null,imageType);

            //转成PDF文件
            wordToPDFUtil.convert(path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".jpg",path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".pdf");

            //附件表增加成交通知书pdf记录
            attachment.setAttachName(imageType+"_"+sdf.format(new Date())+uuid_icon+".pdf");
            attachment.setFileType("application/pdf");
            attachment.setPath(path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".pdf");
            attachment.setUploadTime(new Date());
            attachment.setUploader(jwtUser.getUsername());
            attachment.setProjectCode(projectInfo.getProjectCode());
            attachment = attachmentRepository.save(attachment);

            //成交通知书图片压缩并加密上传
            String password = EncryptZipUtil.zipFile(path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".zip",path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".pdf");
            if("添加压缩文件出错".equals(password)){
                throw new RuntimeException("添加成交通知书的压缩文件出错*****");
            }


            //附件表增加成交通知书加密文件记录
            attachmentEncrypt.setAttachName(imageType+"_"+sdf.format(new Date())+uuid_icon+".zip");
            attachmentEncrypt.setFileType("application/zip");
            attachmentEncrypt.setPath(path+"/"+imageType+"_"+sdf.format(new Date())+uuid_icon+".zip");
            attachmentEncrypt.setUploadTime(new Date());
            attachmentEncrypt.setUploader(jwtUser.getUsername());
            attachmentEncrypt.setPassword(password);
            attachmentEncrypt.setIsSendEmail(0);
            attachmentEncrypt.setProjectCode(projectInfo.getProjectCode());
            attachmentEncrypt = attachmentRepository.save(attachmentEncrypt);

            /*//发送加密文件密码给密码负责人
            boolean sendEmail = sendEmail(imageType + "_" + sdf.format(new Date()) + uuid_icon + ".zip", password, projectInfo.getProjectCode());
            if(!sendEmail){
                throw new RuntimeException("成交通知书的通知邮件发送失败");
            }*/

        }catch (Exception e){
            logger.error("生成成交通知书图片发生异常",e);
        }
        Long proId = Long.valueOf(projectId);
        BidNotice bidNotice = new BidNotice();
        bidNotice.setProjectCode(projectInfo.getProjectCode());
        bidNotice.setProjectSubject(projectInfo.getProjectSubject());
        bidNotice.setAmount(projectInfo.getAmountRmb()+"");
        bidNotice.setSupplier(projectInfoRepository.getAgentName(proId));
        //Constant.NOTICE_MONEY=200000
        if(Double.parseDouble(projectInfo.getAmountRmb().replaceAll(",",""))>=Constant.NOTICE_MONEY){
            bidNotice.setStatus(Constant.STATE_DRAFT);//1:拟稿  7:待办
        }else if(Double.parseDouble(projectInfo.getAmountRmb().replaceAll(",",""))<Constant.NOTICE_MONEY){
            bidNotice.setStatus(Constant.STATE_WAIT_SIGN);//1:拟稿  7:待办
        }
        bidNotice.setCreator(jwtUser.getFullname());
        bidNotice.setCreateDate(new Date());
        bidNotice.setPurchaser(projectInfo.getPurchaser());
        bidNotice.setProjectInfo(projectInfo);
        bidNotice.setAttachment(attachment);
        bidNotice.setAttachmentEncrypt(attachmentEncrypt);
        bidNoticeRepository.save(bidNotice);

        //更新项目表的采购结果附件id字段
        projectInfoRepository.updateNoticeId(attachment.getAttachId(),attachmentEncrypt.getAttachId(),Long.parseLong(projectId));

        return bidNotice;
    }

    //发送密码给密码保管人
    private boolean sendEmail(String zipFileName,String password,String projectCode) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(host);

        message.setTo(email);

        if(zipFileName.contains("采购合同")){
            message.setSubject("采购合同密码");
        }
        message.setSubject("成交通知书");

        message.setText("项目编号:"+projectCode+"的"+zipFileName+"的密码为"+password+",请查收");

        try {

            mailSender.send(message);
            logger.info("测试邮件已发送。");

        } catch (Exception e) {
            logger.error("发送邮件时发生异常了！", e);
            return false;
        }
        return true;
    }

    public BidNoticeCollectionDto to(Page<BidNotice> source, HttpServletRequest request) {
        BidNoticeCollectionDto bidNoticeCollectionDto = new BidNoticeCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, bidNoticeCollectionDto, request);
        BidNoticeDto bidNoticeDto;
        for (BidNotice bidNotice : source) {
            bidNoticeDto = new BidNoticeDto();
            BeanUtils.copyProperties(bidNotice, bidNoticeDto);
            bidNoticeCollectionDto.add(bidNoticeDto);
        }
        return bidNoticeCollectionDto;
    }

    public Specification<BidNotice> getWhereClause(String projectCode, String projectSubject,String status) {
        return (Specification<BidNotice>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(projectCode)) {
                predicates.add(criteriaBuilder.equal(root.get("projectCode").as(String.class), projectCode));
            }
            if (Strings.isNotBlank(projectSubject)) {
                predicates.add(criteriaBuilder.equal(root.get("projectSubject").as(String.class), projectSubject));
            }
            if (Strings.isNotBlank(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status").as(String.class), status));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<BidNotice> findMyBidNotices(String projectCode, String projectSubject,String status, JwtUser user, Pageable pageable) {
        String username = user.getUsername();
        //判断当前用户是什么角色，如果是招标中心经办人或者招标科长或者admin则查询全部采购通知书
        Long roleId = userRepository.findRoleIdByUsername(username);
        if( "5".equals(roleId+"") || "6".equals(roleId+"")|| "7".equals(roleId+"")){
            List<BidNotice> list=bidNoticeRepository.findMyBidNoticesAll(projectCode, projectSubject, status);
            return Common.listConvertToPage(list, pageable);
        }else {
            return Common.listConvertToPage(bidNoticeRepository.findMyBidNotices(projectCode, projectSubject, status,username), pageable);
        }
    }


    public Page<BidNotice> findBidNotices(Specification<BidNotice> specification, Pageable pageable) {
        return bidNoticeRepository.findAll(specification, pageable);
    }

    public BidNotice update(String bidNoticeId) {
        BidNotice model=bidNoticeRepository.findInfoByProjectId(bidNoticeId);
        BidNotice bidNotice = new BidNotice();
        if (null!=model) {
            bidNotice = model;
            if(bidNotice.getStatus()==1){
                bidNotice.setStatus(Constant.STATE_WAIT_SIGN);//拟稿-->待签收
            }else if(bidNotice.getStatus()==7){
                bidNotice.setSignDate(new Date());
                bidNotice.setStatus(Constant.STATE_SIGNED); //待签收-->已签收
            }
            bidNoticeRepository.save(bidNotice);
        }
        return bidNotice;
    }

    public void export(HttpServletResponse response, List<Long> bidNoticeIds) {
        try {
            String[] header = {"项目主题", "项目编号", "成交供应商", "成交金额", "状态", "采购人", "创建人", "创建时间", "签收时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("成交通知书信息表");
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

            List<BidNotice> list;
            if (bidNoticeIds != null && !bidNoticeIds.isEmpty()) {
                list = bidNoticeRepository.findAll(bidNoticeIds);
            } else {
                list = bidNoticeRepository.findAll();
            }
            BidNotice bidNotice;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                bidNotice = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(bidNotice.getProjectSubject()));
                row.createCell(1).setCellValue(new HSSFRichTextString(bidNotice.getProjectCode()));
                row.createCell(2).setCellValue(new HSSFRichTextString(bidNotice.getSupplier() == null ? "" : bidNotice.getSupplier()));
                row.createCell(3).setCellValue(new HSSFRichTextString(bidNotice.getAmount()));
                row.createCell(4).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(bidNotice.getStatus())));
                row.createCell(5).setCellValue(new HSSFRichTextString(bidNotice.getPurchaser()));
                row.createCell(6).setCellValue(new HSSFRichTextString(bidNotice.getCreator()));
                row.createCell(7).setCellValue(new HSSFRichTextString(format.format(bidNotice.getCreateDate())));
                String signDate;
                if (bidNotice.getSignDate() == null) {
                    signDate = "";
                } else {
                    signDate = format.format(bidNotice.getSignDate());
                }
                row.createCell(8).setCellValue(new HSSFRichTextString(signDate));
            }
            response.setHeader("Content-disposition", "attachment;fileName=bidNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            logger.error("导出采购结果通知书发生异常",e);
        }
    }


    public BidNotice getBidNoticeById(String bidNoticeId) {
        return bidNoticeRepository.findById(Long.parseLong(bidNoticeId)).get();
    }
}
