package com.etone.protocolsupply.service.notice;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.notice.BidNoticeDto;
import com.etone.protocolsupply.model.dto.notice.ContractNoticceDto;
import com.etone.protocolsupply.model.dto.notice.ContractNoticeCollectionDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.notice.ContractNotice;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.notice.ContractNoticeRepository;
import com.etone.protocolsupply.repository.project.AgentInfoExpRepository;
import com.etone.protocolsupply.repository.project.PartInfoExpRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.service.AttachmentService;
import com.etone.protocolsupply.utils.*;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
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
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
@PropertySource(value = {
        "classpath:myApplication.properties",
}, encoding = "utf-8")
public class ContractNoticeService {

    private static final Logger logger = LoggerFactory.getLogger(ContractNoticeService.class);

    @Autowired
    private ContractNoticeRepository ContractNoticeRepository;

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Autowired
    private CargoInfoRepository cargoInfoRepository;

    @Autowired
    private PagingMapper pagingMapper;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private PartInfoExpRepository partInfoExpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AgentInfoExpRepository agentInfoExpRepository;

    @Autowired
    private WordToPDFUtil wordToPDFUtil;

    @Value("${file.upload.path.filePath}")
    protected String uploadFilePath;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String host;

    @Value("${email.address}")
    private String email;

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
            User user1=userRepository.findUserInfoWithAgent(contractNotice.getProjectInfo().getProjectId());
            if(user1==null){
                contractNoticceDto.setSupplier("");
            }else {
                contractNoticceDto.setSupplier(user1.getCompany()+"("+user1.getUsername()+")");
            }
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
            response.setHeader("Content-disposition", "attachment;fileName=contractNotice.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            logger.error("导出合同通知书异常",e);
        }
    }

    public ContractNotice getContractNoticeById(String contractNoticeId) {
        return ContractNoticeRepository.findById(Long.parseLong(contractNoticeId)).get();
    }

    public ContractNotice save(String projectId, JwtUser user) {
        CargoInfo cargoInfo=cargoInfoRepository.findAllByProjectId(Long.parseLong(projectId));
        String typeMsg=cargoInfo.getType();//1：国产 2：进口
        String type="";
        if(typeMsg.equals("国产")){
            type="1";
        }else {
            type="2";
        }

        //查询合同模板所在路径
        Attachment attachmentmob = attachmentService.findContractTemplate(type);
        String path =attachmentmob.getPath();

        //未加密附件
        Attachment attachment = new Attachment();
        //加密附件
        Attachment attachmentEncrypt = new Attachment();
        //查询项目详情
        ProjectInfo projectInfo = projectInfoRepository.findAllByProjectId(Long.valueOf(projectId));

        String Currency=projectInfo.getCurrency();//货币单位
        //根据项目id查询代理商所在公司名称
        String agentCompanyName = agentInfoExpRepository.findAgentCompanyName(Long.valueOf(projectId));
        HashMap<String, String> contentMap = new HashMap<>();
        if(type.equals("1")){
            //国产
            contentMap.put("${NAME}",agentCompanyName+"");//乙方名称
            contentMap.put("${CARGONAME}",projectInfo.getCargoName()+"");//设备名称
            contentMap.put("${DONETIME}",projectInfo.getDeliveryDate()+"");//交货时间
            contentMap.put("${PAYADDRESS}",projectInfo.getPriceTerm()+"");//交货地点
            contentMap.put("${INSTRUCTION}",projectInfo.getPacking_instruction()+"");//包装要求
            contentMap.put("${CHMONEY}", ConvertUpMoney.toChinese(projectInfo.getAmount()+""));//项目总金额（计算汇率之前）
            contentMap.put("${MONEY}",projectInfo.getAmount()+"");//项目总金额rmb（计算汇率之后）
            String str=projectInfo.getPaymentMethod();
            contentMap.put("${MONEYTYPE}",str.substring(0,1));//付款方式
            contentMap.put("${ALLTOTAL}",projectInfo.getAmount()+" "+Currency);
        }else {
            //进口
            contentMap.put("${NAME}",agentCompanyName+"");//乙方名称
            contentMap.put("${CARGONAME}",projectInfo.getCargoName()+"");//设备名称
            contentMap.put("${DONETIME}",projectInfo.getDeliveryDate()+"");//交货时间
            contentMap.put("${PAYADDRESS}",projectInfo.getPriceTerm()+"");//交货地点
            contentMap.put("${MONEY}",projectInfo.getAmount()+"");//项目总金额rmb（计算汇率之后）
            String priceTransactionWay=projectInfo.getPrice_transaction_way();//价格成交方式
            contentMap.put("${PAYWAY}",priceTransactionWay.substring(0,1));//价格成交方式
            String str=projectInfo.getPaymentMethod();
            contentMap.put("${MONEYTYPE}",str.substring(0,1));//支付方法
            contentMap.put("${ALLTOTAL}",projectInfo.getAmount()+" "+Currency);
            contentMap.put("${FOREIGNCO}",projectInfo.getForeign_trade_company()+"");//外贸合同境外公司签订方

        }
        XWPFDocument document;
        try{
            //本地测试
            //path ="D:\\contractTemplate1.docx";//国产模板
            //path ="D:\\contractTemplate2.docx";//进口模板
            document = new XWPFDocument(new FileInputStream(new File(path)));

            //获取文件中的所有表格
            List<XWPFTable> tables = document.getTables();
            XWPFTable table1 = tables.get(0);//附件一：设备清单及相关约定
            XWPFTable table2 = tables.get(1);//附件二：保修服务表
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
                    for (int i = 0; i < expList.size() + 3; i++) {
                        table1.createRow();
                    }
                    for (int i = 0; i < expList.size() - 1; i++){
                        table2.createRow();
                    }
                }

                //填充数据-附件一
                for (int i = 0; i < expList.size(); i++) {
                    table1.getRow(i+1).getCell(0).setText(i+1+"");//序号
                    table1.getRow(i+1).getCell(1).setText(expList.get(i).getPartName());//配件名称
                    table1.getRow(i+1).getCell(2).setText(expList.get(i).getStandards());//型号/品牌/规格
                    table1.getRow(i+1).getCell(3).setText(expList.get(i).getManufactor());//产地
                    table1.getRow(i+1).getCell(4).setText(expList.get(i).getTechParams());//主要技术参数
                    table1.getRow(i+1).getCell(5).setText(expList.get(i).getUnit());//单位
                    table1.getRow(i+1).getCell(6).setText(expList.get(i).getQuantity());//数量
                    table1.getRow(i+1).getCell(7).setText(expList.get(i).getPrice()+"");//单价
                    table1.getRow(i+1).getCell(8).setText(expList.get(i).getTotal()+"");//合计
                    table1.getRow(i+1).getCell(9).setText(expList.get(i).getRemark());//备注
                }

                //使用第expList.size()+1行--合计行
                List<XWPFTableCell> tableName = table1.getRow(expList.size()+1).getTableCells();
                //将第一列到第四列合并
                for (int i = 1; i <= 8; i++) {
                    //对单元格进行合并的时候,要标志单元格是否为起点,或者是否为继续合并
                    if (i == 1)
                        tableName.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);//这是起点
                    else
                        tableName.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);//继续合并
                }
                if(type.equals("1")){
                    tableName.get(1).setText(projectInfo.getAmount()+"(元)");//合计
                }else {
                    tableName.get(1).setText(projectInfo.getAmount()+" "+Currency);//合计
                }
                tableName.get(2).setText(projectInfo.getRemark());//项目备注


                //使用第expList.size()+2行--外贸合同境外公司签订方
                List<XWPFTableCell> tableName2 = table1.getRow(expList.size()+2).getTableCells();
                //使用第expList.size()+3行--交货时间：
                List<XWPFTableCell> tableName3 = table1.getRow(expList.size()+3).getTableCells();
                //使用第expList.size()+3行--交货地点：
                List<XWPFTableCell> tableName4 = table1.getRow(expList.size()+4).getTableCells();
                //将第零列到第屎列合并
                for (int i = 0; i <= 9; i++) {
                    //对单元格进行合并的时候,要标志单元格是否为起点,或者是否为继续合并
                    if (i == 0){
                        tableName2.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);//这是起点
                        tableName3.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);//这是起点
                        tableName4.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);//这是起点
                    }else {
                        tableName2.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);//继续合并
                        tableName3.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);//继续合并
                        tableName4.get(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);//继续合并
                    }
                }
                table1.getRow(expList.size()+1).getCell(0).setText("合计");
                if(type.equals("1")){
                    //国产模板--包装要求
                    table1.getRow(expList.size()+2).getCell(0).setText("包装要求："+projectInfo.getPacking_instruction()+"");
                }else {
                    //进口模板--外贸合同境外公司签订方
                    table1.getRow(expList.size()+2).getCell(0).setText("外贸合同境外公司签订方："+projectInfo.getForeign_trade_company()+"");
                }
                table1.getRow(expList.size()+3).getCell(0).setText("交货时间："+projectInfo.getDeliveryDate()+"");
                table1.getRow(expList.size()+4).getCell(0).setText("交货地点："+projectInfo.getPriceTerm()+"");

                //填充数据-附件二
                for (int i = 0; i < expList.size(); i++) {
                    table2.getRow(i+1).getCell(0).setText(i+1+"");//序号
                    table2.getRow(i+1).getCell(1).setText(expList.get(i).getPartName());//配件名称
                    table2.getRow(i+1).getCell(2).setText(expList.get(i).getGuarantee_date());//质保期
                    table2.getRow(i+1).getCell(3).setText(expList.get(i).getWarranty_date());//保修响应时间
                    table2.getRow(i+1).getCell(4).setText(expList.get(i).getAfter_sales_service_outlets_and_number());//售后服务网点
                    table2.getRow(i+1).getCell(5).setText(expList.get(i).getRemark());//备注
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
            wordToPDFUtil.convert(wordPath+uuid+".docx",wordPath+"/"+"采购合同_"+sdf.format(new Date())+uuid+".pdf");
            //合同文件压缩并加密上传
            String password = EncryptZipUtil.zipFile(wordPath+"/"+"采购合同_"+sdf.format(new Date())+uuid+".zip",wordPath+"/"+"采购合同_"+sdf.format(new Date())+uuid+".pdf");
            if("添加压缩文件出错".equals(password)){
                throw new RuntimeException("添加合同的压缩文件出错*****");
            }
            //附件表增加记录
            attachment.setAttachName("采购合同_"+sdf.format(new Date())+uuid+".pdf");
            attachment.setFileType("application/pdf");
            attachment.setPath(wordPath+"/"+"采购合同_"+sdf.format(new Date())+uuid+".pdf");
            attachment.setUploadTime(new Date());
            attachment.setUploader(user.getUsername());
            attachmentRepository.save(attachment);


            //附件表增加合同加密文件记录
            attachmentEncrypt.setAttachName("采购合同_"+sdf.format(new Date())+uuid+".zip");
            attachmentEncrypt.setFileType("application/zip");
            attachmentEncrypt.setPath(wordPath+"/"+"采购合同_"+sdf.format(new Date())+uuid+".zip");
            attachmentEncrypt.setUploadTime(new Date());
            attachmentEncrypt.setUploader(user.getUsername());
            attachmentEncrypt.setPassword(password);
            attachmentEncrypt.setIsSendEmail(0);
            attachmentEncrypt.setProjectCode(projectInfo.getProjectCode());
            attachmentEncrypt = attachmentRepository.save(attachmentEncrypt);

            /*//发送加密文件密码给密码负责人
            boolean sendEmail = sendEmail("采购合同_" + sdf.format(new Date()) + uuid + ".zip", password, projectInfo.getProjectCode());
            if(!sendEmail){
                throw new RuntimeException("采购合同的通知邮件发送失败");
            }*/

        }catch (Exception e){
            logger.error("生成合同时异常",e);
        }


        ContractNotice contractNotice = null;
        try {
            Long proId = Long.valueOf(projectId);
            contractNotice = new ContractNotice();
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
            contractNotice.setAttachmentEncrypt(attachmentEncrypt);
            ContractNoticeRepository.save(contractNotice);


            //TODO 更新工程表的附件字段
            projectInfoRepository.updateContractId(attachment.getAttachId(),attachmentEncrypt.getAttachId(),Long.parseLong(projectId));
        } catch (Exception e) {
            logger.error("合同保存时异常",e);
        }

        return contractNotice;
    }

    //发送密码给密码保管人
    private boolean sendEmail(String zipFileName,String password,String projectCode) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(host);

        message.setTo(email);

        message.setSubject("采购合同密码");

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

    public Page<ContractNotice> getContractByCondition(String projectCode, String projectSubject, JwtUser user, Pageable pageable) {
        //返回数据
        List<ContractNotice> contractNoticeList =new ArrayList<>();

        //当前登录人名称
        String username = user.getUsername();

        //判断当前用户是什么角色，如果是招标中心经办人或者招标科长则查询全部合同
        Long roleId = userRepository.findRoleIdByUsername(username);

        if("5".equals(roleId+"") || "6".equals(roleId+"")|| "7".equals(roleId+"")){
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
