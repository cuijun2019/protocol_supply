package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.ResultNoticeCollectionDto;
import com.etone.protocolsupply.model.dto.notice.ResultNoticeDto;
import com.etone.protocolsupply.model.dto.project.ProjectInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.notice.ResultNotice;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.notice.ResultNoticeRepository;
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
public class ResultNoticeService {

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
            response.setHeader("Content-disposition", "attachment;filename=resultNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProjectInfo getRelateProject(String resultNoticeId) {
        return resultNoticeRepository.findById(Long.parseLong(resultNoticeId)).get().getProjectInfo();
    }

    public ResultNotice save(String projectId, JwtUser user, String templatePath) {
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

            /*//文件类型
            String fileType = templatePath.substring(templatePath.lastIndexOf(".") + 1);

            //生成采购结果通知书并上传保存
            InputStream is = new FileInputStream(templatePath);


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
                    if("学生就业指导中心".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(creator.getCompany());
                    }
                    if("FW008".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(projectInfo.getProjectCode());
                    }
                    if("毕业典礼和学位授予仪式布展".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(projectInfo.getProjectSubject());
                    }
                    if("供应商成交".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(projectInfo.getCreator());
                    }
                    if("5000".equals(formatter.formatCellValue(cell))){
                        cell.setCellValue(projectInfo.getAmountRmb());
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

            String imageType="采购结果通知书";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String path = uploadFilePath + Common.getYYYYMMDate(new Date());

            //生成采购结果通知书图片
            ImageUtil.getImage(projectInfo,creator,imageType,path,path+"/"+imageType+"_"+sdf.format(new Date())+uuid+".jpg","");

            //附件表增加记录
            attachment.setAttachName(imageType+"_"+sdf.format(new Date())+uuid+".jpg");
            attachment.setFileType("image/jpeg");
            attachment.setPath(path+"/"+imageType+"_"+sdf.format(new Date())+uuid+".jpg");
            attachment.setUploadTime(new Date());
            attachment.setUploader(user.getUsername());
            attachment = attachmentRepository.save(attachment);

        }catch (Exception e){
            e.printStackTrace();
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
