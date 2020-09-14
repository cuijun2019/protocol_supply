package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.ContractNoticceDto;
import com.etone.protocolsupply.model.dto.notice.ContractNoticeCollectionDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.notice.ContractNotice;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.notice.ContractNoticeRepository;
import com.etone.protocolsupply.repository.project.PartInfoExpRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.ConvertUpMoney;
import com.etone.protocolsupply.utils.PagingMapper;
import com.etone.protocolsupply.utils.WordToPDFUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
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
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class ContractNoticeService {

    private static final Logger logger = LoggerFactory.getLogger(ContractNoticeService.class);

    @Autowired
    private ContractNoticeRepository ContractNoticeRepository;

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Autowired
    private PagingMapper pagingMapper;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private PartInfoExpRepository partInfoExpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CargoInfoRepository cargoInfoRepository;

    @Value("${file.upload.path.filePath}")
    protected String uploadFilePath;

    public Specification<ContractNotice> getWhereClause(String projectCode, String projectSubject) {
        return (Specification<ContractNotice>) (root, criteriaQuery, criteriaBuilder) -> {

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

    public Page<ContractNotice> findContractNotice(Specification<ContractNotice> specification, Pageable pageable) {
        return ContractNoticeRepository.findAll(specification,pageable);
    }

    public ContractNoticeCollectionDto to(Page<ContractNotice> page, HttpServletRequest request) {
        ContractNoticeCollectionDto contractNoticeCollectionDto = new ContractNoticeCollectionDto();
        pagingMapper.storeMappedInstanceBefore(page, contractNoticeCollectionDto, request);
        ContractNoticceDto contractNoticceDto;
        for (ContractNotice contractNotice : page) {
            contractNoticceDto = new ContractNoticceDto();
            BeanUtils.copyProperties(contractNotice, contractNoticceDto);
            contractNoticeCollectionDto.add(contractNoticceDto);
        }
        return contractNoticeCollectionDto;
    }

    public ContractNotice update(String projectId) {
        ContractNotice model=ContractNoticeRepository.findInfoByProjectId(projectId);
        ContractNotice contractNotice = new ContractNotice();
        if (null!=model) {
            contractNotice = model;
            contractNotice.setStatus(Constant.STATE_SIGNED);
            contractNotice.setSignDate(new Date());
            ContractNoticeRepository.save(contractNotice);
        }
        return contractNotice;
    }

    public void export(HttpServletResponse response, List<Long> contractNoticeIds) {
        try {
            String[] header = {"项目主题", "项目编号", "成交供应商", "成交金额", "状态", "采购人", "创建人", "创建时间", "签收时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("合同信息表");
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

            List<ContractNotice> list = new ArrayList<>();
            if (contractNoticeIds != null && !contractNoticeIds.isEmpty()) {
                list = ContractNoticeRepository.findAll(contractNoticeIds);
            }else {
                list = ContractNoticeRepository.findAll();
            }
            ContractNotice contractNotice;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                contractNotice = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(contractNotice.getProjectSubject()));
                row.createCell(1).setCellValue(new HSSFRichTextString(contractNotice.getProjectCode()));
                row.createCell(2).setCellValue(new HSSFRichTextString(contractNotice.getSupplier()==null?"":contractNotice.getSupplier()));
                row.createCell(3).setCellValue(new HSSFRichTextString(contractNotice.getAmount()));
                row.createCell(4).setCellValue(new HSSFRichTextString(Constant.REVIEW_STATUS_MAP.get(contractNotice.getStatus())));
                row.createCell(5).setCellValue(new HSSFRichTextString(contractNotice.getPurchaser()));
                row.createCell(6).setCellValue(new HSSFRichTextString(contractNotice.getCreator()));
                row.createCell(7).setCellValue(new HSSFRichTextString(format.format(contractNotice.getCreateDate())));
                String signDate;
                if(contractNotice.getSignDate()==null){
                    signDate="";
                }else {
                    signDate=format.format(contractNotice.getSignDate());
                }
                row.createCell(8).setCellValue(new HSSFRichTextString(signDate));
            }
            response.setHeader("Content-disposition", "attachment;filename=contractNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            logger.error("导出合同通知书异常",e.getMessage());
        }
    }

    public ContractNotice getContractNoticeById(String contractNoticeId) {
        return ContractNoticeRepository.findById(Long.parseLong(contractNoticeId)).get();
    }

    public ContractNotice save(String projectId, JwtUser user, String path) {
        //
        Attachment attachment = new Attachment();

        //查询项目详情
        ProjectInfo projectInfo = projectInfoRepository.findAllByProjectId(Long.valueOf(projectId));

        //查询创建人所在公司
        User creator = userRepository.findByUsername(projectInfo.getCreator());

        //根据项目id查询配件表得到货物
        List<CargoInfo> cargoInfoList = cargoInfoRepository.findByProjectId(Long.parseLong(projectId));


        HashMap<String, String> contentMap = new HashMap<>();
        contentMap.put("${NAME}",creator.getCompany());
        contentMap.put("${GOODS}",projectInfo.getProjectSubject());
        contentMap.put("${EQUIPMENT}",cargoInfoList.get(0).getCargoName());
        contentMap.put("${AMOUNT}",projectInfo.getQuantity());
        contentMap.put("${DONETIME}",projectInfo.getDeliveryDate()+"");
        contentMap.put("${CHMONEY}", ConvertUpMoney.toChinese(projectInfo.getAmountRmb()+""));
        contentMap.put("${MONEY}",projectInfo.getAmountRmb()+"");
        contentMap.put("${MONEYTYPE}",projectInfo.getPaymentMethod());
        contentMap.put("${CHDAY}",ConvertUpMoney.toChinese(projectInfo.getGuaranteeDate()).substring(0,ConvertUpMoney.toChinese(projectInfo.getGuaranteeDate()).length()-1));
        XWPFDocument document;


        try{
            //本地测试
            //path ="D:\\合同模板.docx";
            document = new XWPFDocument(new FileInputStream(new File(path)));

            //获取文件中的所有表格
            List<XWPFTable> tables = document.getTables();
            XWPFTable table = tables.get(0);

            ArrayList<PartInfoExp> expList = new ArrayList<>();

            //查询配件列表
            List<PartInfoExp> partInfoExpList = partInfoExpRepository.findByProjectId(Long.parseLong(projectId));

            if(partInfoExpList!=null && partInfoExpList.size()>0){
                for (int i = 0; i < partInfoExpList.size(); i++) {
                    expList.add(partInfoExpList.get(i));
                }
            }


            if(expList!=null){
                //根据配件表集合大小增加空白单元格
                if(expList.size()>1){
                    for (int i = 0; i < expList.size() - 1; i++) {
                        table.createRow();
                    }
                }

                //填充数据
                for (int i = 0; i < expList.size(); i++) {
                    table.getRow(i+1).getCell(0).setText(expList.get(i).getPartName());
                    table.getRow(i+1).getCell(1).setText(expList.get(i).getStandards());
                    table.getRow(i+1).getCell(2).setText(expList.get(i).getManufactor());
                    table.getRow(i+1).getCell(3).setText(expList.get(i).getTechParams());
                    table.getRow(i+1).getCell(4).setText(expList.get(i).getUnit());
                    table.getRow(i+1).getCell(5).setText(expList.get(i).getQuantity());
                    table.getRow(i+1).getCell(6).setText(expList.get(i).getPrice()+"");
                    BigDecimal quantity = new BigDecimal(expList.get(i).getQuantity());
                    BigDecimal price = new BigDecimal(Double.toString(expList.get(i).getPrice()));
                    table.getRow(i+1).getCell(7).setText(quantity.multiply(price)+"");
                }
            }


            // 获取word中的所有段落，替换目标文字
            Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
            while (itPara.hasNext()) {
                XWPFParagraph paragraph = itPara.next();
                List<XWPFRun> runs = paragraph.getRuns();
                for (int i = 0; i < runs.size(); i++) {
                    String oneparaString = runs.get(i).getText(runs.get(i).getTextPosition());
                    for (Map.Entry<String, String> entry : contentMap.entrySet()) {
                        if(oneparaString!=null && oneparaString.contains(entry.getKey())){
                            oneparaString = oneparaString.replace(entry.getKey(), entry.getValue());
                        }
                    }
                    runs.get(i).setText(oneparaString, 0);
                }
            }

            String wordPath = uploadFilePath + Common.getYYYYMMDate(new Date());

            File uploadPath = new File(wordPath);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            //导出到文件
            UUID uuid = UUID.randomUUID();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.write(byteArrayOutputStream);
            OutputStream outputStream = new FileOutputStream(wordPath+uuid+".docx");
            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.close();


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            //word转PDF保存
            WordToPDFUtil.convert(wordPath+uuid+".docx",wordPath+"/"+"采购合同_"+sdf.format(new Date())+uuid+".pdf");

            //附件表增加记录
            attachment.setAttachName("采购合同_"+sdf.format(new Date())+uuid+".pdf");
            attachment.setFileType("application/pdf");
            attachment.setPath(wordPath+"/"+"采购合同_"+sdf.format(new Date())+uuid+".pdf");
            attachment.setUploadTime(new Date());
            attachment.setUploader(user.getUsername());
            attachmentRepository.save(attachment);


        }catch (Exception e){
            logger.error("生成合同通知书图片时异常",e.getMessage());
        }


        Long proId = Long.valueOf(projectId);
        ContractNotice contractNotice = new ContractNotice();
        contractNotice.setProjectCode(projectInfo.getProjectCode());
        contractNotice.setProjectSubject(projectInfo.getProjectSubject());
        contractNotice.setAmount(projectInfo.getAmountRmb()+"");
        contractNotice.setSupplier(projectInfoRepository.getAgentName(proId));
        contractNotice.setStatus(Constant.STATE_WAIT_SIGN);
        contractNotice.setCreator(user.getFullname());
        contractNotice.setCreateDate(new Date());
        contractNotice.setPurchaser(projectInfo.getPurchaser());
        contractNotice.setProjectInfo(projectInfo);
        contractNotice.setAttachment(attachment);
        ContractNoticeRepository.save(contractNotice);


        //TODO 更新工程表的附件字段
        projectInfoRepository.updateContractId(attachment.getAttachId(),Long.parseLong(projectId));

        return contractNotice;
    }

    public Page<ContractNotice> getContractByCondition(String projectCode, String projectSubject, JwtUser user, Pageable pageable) {
        //返回数据
        List<ContractNotice> contractNoticeList =new ArrayList<>();

        //当前登录人名称
        String username = user.getUsername();

        //判断当前用户是什么角色，如果是招标中心经办人或者招标科长则查询全部合同
        Long roleId = userRepository.findRoleIdByUsername(username);

        if("5".equals(roleId+"") || "6".equals(roleId+"")){
            contractNoticeList = ContractNoticeRepository.findByCondition(projectCode,projectSubject);
        }

        //如果是供应商或者代理商则查询跟其有关的合同
        if("1".equals(roleId+"")){//供应商
            contractNoticeList = ContractNoticeRepository.findBySupplierCondition(projectCode,projectSubject,username);
        }else if("2".equals(roleId+"")){//代理商
            contractNoticeList = ContractNoticeRepository.findByAgentCondition(projectCode,projectSubject,username);
        }
        return Common.listConvertToPage(contractNoticeList,pageable);
    }
}
