package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.BidNoticeCollectionDto;
import com.etone.protocolsupply.model.dto.notice.BidNoticeDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.notice.BidNoticeRepository;
import com.etone.protocolsupply.repository.project.AgentInfoExpRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.ImageUtil;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class BidNoticeService {

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

    @Value("${file.upload.path.filePath}")
    protected String uploadFilePath;

    public BidNotice save(String projectId, JwtUser jwtUser, String bidTemplatePath) {
        Attachment attachment = new Attachment();

        //查询项目详情
        ProjectInfo projectInfo = projectInfoRepository.findAllByProjectId(Long.valueOf(projectId));

        //根据项目id查询代理商名称
        List<AgentInfoExp> agentInfoExp = agentInfoExpRepository.findByProjectId(Long.valueOf(projectId));

        try{

            //查询创建人所在公司
            User creator = userRepository.findByUsername(projectInfo.getCreator());

            /*//文件类型
            String fileType = bidTemplatePath.substring(bidTemplatePath.lastIndexOf(".") + 1);

            //生成成交通知书并上传保存
            InputStream is = new FileInputStream(bidTemplatePath);

            //获取工作薄
            Workbook wb = null;
            if (fileType.equals("xls")) {
                wb = new HSSFWorkbook(is);
            } else if (fileType.equals("xlsx")) {
                wb = new XSSFWorkbook(is);
            }

            //读取第一个工作页sheet
            Sheet sheet = wb.getSheetAt(0);
            //第一行为标题
            for (Row row : sheet) {
                for (Cell cell : row) {
                    //根据不同类型转化成字符串
                    cell.setCellType(CellType.STRING);
                    DataFormatter formatter = new DataFormatter();
                    if("成交供应商名称:".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(agentInfoExp.get(0).getAgentName());
                    }
                    if("FW008".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(projectInfo.getProjectCode());
                    }
                    if("FW009".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(projectInfo.getProjectSubject());
                    }
                    if("FW010".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(projectInfo.getAmountRmb());
                    }
                    if("FW011".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(creator.getCompany());
                    }
                    if("2018年6月6日".equals(formatter.formatCellValue(cell).trim())){
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                        cell.setCellValue(simpleDateFormat.format(date));
                    }
                }
            }

            //改名保存
            UUID uuid = UUID.randomUUID();
            FileOutputStream excelFileOutPutStream = new FileOutputStream(uploadFilePath+uuid+".xlsx");
            wb.write(excelFileOutPutStream);
            excelFileOutPutStream.flush();
            excelFileOutPutStream.close();*/

            String uuid = UUID.randomUUID().toString().substring(0,8);

            String imageType="中标通知书";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String path = uploadFilePath + Common.getYYYYMMDate(new Date());

            //生成采购结果通知书图片
            ImageUtil.getImage(projectInfo,creator,imageType,path,path+"/"+imageType+"_"+sdf.format(new Date())+uuid+".jpg",agentInfoExp.get(0).getAgentName());

            //附件表增加记录
            attachment.setAttachName(imageType+"_"+sdf.format(new Date())+uuid+".jpg");
            attachment.setFileType("image/jpeg");
            attachment.setPath(path+"/"+imageType+"_"+sdf.format(new Date())+uuid+".jpg");
            attachment.setUploadTime(new Date());
            attachment.setUploader(jwtUser.getUsername());
            attachment = attachmentRepository.save(attachment);

        }catch (Exception e){
            e.printStackTrace();
        }
        Long proId = Long.valueOf(projectId);
        BidNotice bidNotice = new BidNotice();
        bidNotice.setProjectCode(projectInfo.getProjectCode());
        bidNotice.setProjectSubject(projectInfo.getProjectSubject());
        bidNotice.setAmount(projectInfo.getAmountRmb()+"");
        bidNotice.setSupplier(projectInfoRepository.getAgentName(proId));
        //Constant.NOTICE_MONEY=200000
        if(projectInfo.getAmountRmb()>=Constant.NOTICE_MONEY){
            bidNotice.setStatus(Constant.STATE_DRAFT);//1:拟稿  7:待办
        }else if(projectInfo.getAmountRmb()<Constant.NOTICE_MONEY){
            bidNotice.setStatus(Constant.STATE_WAIT_SIGN);//1:拟稿  7:待办
        }
        bidNotice.setCreator(jwtUser.getFullname());
        bidNotice.setCreateDate(new Date());
        bidNotice.setPurchaser(projectInfo.getPurchaser());
        bidNotice.setProjectInfo(projectInfo);
        bidNotice.setAttachment(attachment);
        bidNoticeRepository.save(bidNotice);

        //更新项目表的采购结果附件id字段
        projectInfoRepository.updateNoticeId(attachment.getAttachId(),Long.parseLong(projectId));

        return bidNotice;
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
            response.setHeader("Content-disposition", "attachment;filename=bidNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public BidNotice getBidNoticeById(String bidNoticeId) {
        return bidNoticeRepository.findById(Long.parseLong(bidNoticeId)).get();
    }
}
