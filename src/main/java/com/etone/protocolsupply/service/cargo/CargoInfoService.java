package com.etone.protocolsupply.service.cargo;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.ExcelHeaderColumnPojo;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.cargo.CargoCollectionDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.BrandItem;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.BrandItemRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.cargo.PartInfoRepository;
import com.etone.protocolsupply.repository.project.PartInfoExpRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.service.project.ProjectInfoService;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.json.JSONObject;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class CargoInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CargoInfoService.class);
    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private PartInfoService      partInfoService;
    @Autowired
    private PartInfoRepository   partInfoRepository;
    @Autowired
    private PartInfoExpRepository partInfoExpRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private PagingMapper         pagingMapper;
    @Autowired
    private BrandItemRepository brandItemRepository;
    @Autowired
    private PartnerInfoRepository partnerInfoRepository;

    @Autowired
    private UserRepository userRepository;

    public CargoInfo save(CargoInfoDto cargoInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        CargoInfo cargoInfo = new CargoInfo();
        BeanUtils.copyProperties(cargoInfoDto, cargoInfo);
        cargoInfo.setIsDelete(Constant.DELETE_NO);
        cargoInfo.setIsUpdate(Constant.UPDATW_NO);//是否变更：1 是；2：否
        cargoInfo.setCreator(userName);
        cargoInfo.setCreateDate(date);
        cargoInfo.setMaintenanceDate(date);
        cargoInfo.setMaintenanceMan(userName);
        //cargoInfo.setStatus(1);
//        Attachment attachment = cargoInfoDto.getAttachment();
//        if (attachment != null && attachment.getAttachId()!=null && !attachment.getAttachId().equals("")) {
//            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
//            if (optional.isPresent()) {
//                cargoInfo.setAttachment(optional.get());
//            }
//        }else {
//            cargoInfo.setAttachment(null);
//        }
        //PartnerInfo partnerInfo= cargoInfoDto.getPartnerInfo();
        if ( cargoInfoDto.getPartnerId()!=null && !cargoInfoDto.getPartnerId().equals("")) {
            Optional<PartnerInfo> optional = partnerInfoRepository.findById(cargoInfoDto.getPartnerId());
                cargoInfo.setPartnerId(cargoInfoDto.getPartnerId());
        }else {
            cargoInfo.setPartnerId(null);
        }
        cargoInfo.setCargoSerial(this.findLastCargoSerial());
        cargoInfo.setCargoCode(cargoInfo.getItemCode() + cargoInfo.getCargoSerial());

        Set<PartInfo> partInfos = cargoInfoDto.getPartInfos();
        if (partInfos != null && !partInfos.isEmpty()) {
            String partSerial = partInfoService.findLastPartSerial(cargoInfo.getCargoSerial());
            int step = 0;
            double total = 0.00;
            for (PartInfo partInfo : partInfos) {
                total+=partInfo.getTotal();
                if (step == 0) {
                    partInfo.setPartSerial(Common.convertSerial(partSerial, 0));
                } else {
                    partInfo.setPartSerial(Common.convertSerial(partSerial, 1));
                }
                partInfo.setPartCode(cargoInfo.getCargoCode() + partInfo.getPartSerial());
                partInfo.setIsDelete(Constant.DELETE_NO);
                step++;
            }
            cargoInfo.setReprice(total);//货物的参考价格
        }

        cargoInfo = cargoInfoRepository.save(cargoInfo);
        List<Long> partIds = new ArrayList<>();
        if(partInfos.size()>0){
            for (PartInfo partInfo : cargoInfo.getPartInfos()) {
                partIds.add(partInfo.getPartId());
            }
            partInfoRepository.setCargoId(cargoInfo.getCargoId(), partIds);
        }

        return cargoInfo;
    }

    public Specification<CargoInfo> getWhereClause(String isDelete) {
        return (Specification<CargoInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<CargoInfo> findCargoInfos(String isDelete,String isUpdate, String cargoName,String cName, String cargoCode,String actor,Integer status, Pageable pageable) {
        if(null == actor ||actor.equals("admin")){
            return Common.listConvertToPage(cargoInfoRepository.findAll(isDelete,isUpdate, cargoName, cargoCode,status), pageable);
        }else {
            return Common.listConvertToPage(cargoInfoRepository.findAllMyCargo(isDelete,isUpdate, cargoName,cargoCode,actor,status,cName), pageable);
        }

    }

    public Page<CargoInfo> findCargoInfoUpdateList(String isUpdate, String cargoName,String cargoCode,String oldCargoId, Pageable pageable) {

            return Common.listConvertToPage(cargoInfoRepository.findCargoInfoUpdateList(isUpdate, cargoName,cargoCode,oldCargoId), pageable);

    }

    public CargoCollectionDto to(Page<CargoInfo> source, HttpServletRequest request) {
        CargoCollectionDto cargoCollectionDto = new CargoCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, cargoCollectionDto, request);
        CargoInfoDto cargoInfoDto;
        for (CargoInfo cargoInfo : source) {
            cargoInfoDto = new CargoInfoDto();
            BeanUtils.copyProperties(cargoInfo, cargoInfoDto);
            //根据货物id查询审核流程被使用的配件表，查询是否货物被应用
            List<PartInfoExp> count= partInfoExpRepository.selectCountByCargoId(cargoInfo.getCargoId());
            if(count.size()>0){
                cargoInfoDto.setSfbyy(Constant.BEUSE_YES);//是否被引用
            }else {
                cargoInfoDto.setSfbyy(Constant.BEUSE_NO);
            }
            if(null!=cargoInfo.getPartnerId()){
                Optional<PartnerInfo> optional=partnerInfoRepository.findById(cargoInfo.getPartnerId());
                if(optional.isPresent()){
                    cargoInfoDto.setPartnerInfo(optional.get());
                }
                User user=userRepository.findByPartnerId(cargoInfo.getPartnerId());
                if(null!=user){
                    cargoInfoDto.setFullName(user.getFullname());//联系人
                    cargoInfoDto.setTelephone(user.getTelephone());//联系人方式
                }
            }
            cargoCollectionDto.add(cargoInfoDto);
        }
        return cargoCollectionDto;
    }

    public void delete(List<Long> cargoIds) {
        cargoInfoRepository.updateIsDeleteBath(cargoIds);

    }

    public CargoInfoDto findOne(Long cargoId) {
        CargoInfoDto cargoInfoDto=new CargoInfoDto();
        CargoInfo cargoInfo=new CargoInfo();
        Optional<CargoInfo> optional = cargoInfoRepository.findById(cargoId);
        if (optional.isPresent()) {
            cargoInfo=optional.get();
            BeanUtils.copyProperties(cargoInfo, cargoInfoDto);
            if(null!=cargoInfo.getPartnerId()){
                Optional<PartnerInfo> partnerInfo =partnerInfoRepository.findById(cargoInfo.getPartnerId());
                if(partnerInfo.isPresent()){
                    cargoInfoDto.setPartnerInfo(partnerInfo.get());
                }
            }
            return cargoInfoDto;
        } else {
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过货物id"));
        }
    }

    //货物修改edit
    public CargoInfo edit(CargoInfo cargoInfo, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();

        String userName = jwtUser.getUsername();
        CargoInfo cargoInfo1=new CargoInfo();
        partInfoRepository.deleteByCargoId(cargoInfo.getCargoId());
        cargoInfo.setManufactor(userName);
        cargoInfo.setMaintenanceDate(date);
        Set<PartInfo> partInfos =cargoInfo.getPartInfos();
        if (partInfos != null && !partInfos.isEmpty()) {

           String partSerial= partInfoService.findLastPartSerial(cargoInfo.getCargoSerial());
            int step = 0;
            for (PartInfo partInfo : partInfos) {
                if (step == 0) {
                    partInfo.setPartSerial(Common.convertSerial(partSerial, 0));
                } else {
                    partInfo.setPartSerial(Common.convertSerial(partSerial, 1));
                }
                partInfo.setPartCode(cargoInfo.getCargoCode() + partInfo.getPartSerial());
                partInfo.setIsDelete(Constant.DELETE_NO);
                step++;
            }
            cargoInfo.setPartInfos(partInfos);
            cargoInfo1=cargoInfoRepository.save(cargoInfo);
            List<Long> partIds = new ArrayList<>();
            if(partInfos.size()>0){
                for (PartInfo partInfo : cargoInfo1.getPartInfos()) {
                    partIds.add(partInfo.getPartId());
                }
                partInfoRepository.setCargoId(cargoInfo1.getCargoId(), partIds);
            }
        }

        return cargoInfo1;
    }


    //变更=原数据修改+新数据新增
    public CargoInfo update(CargoInfo cargoInfo, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        CargoInfo model=new CargoInfo();
        model = cargoInfoRepository.findAllByCargoId(cargoInfo.getCargoId());

        model.setMaintenanceMan(userName);
        model.setMaintenanceDate(date);
        if(model.getOldCargoId()==null){
            model.setOldCargoId(model.getCargoId());
        }

        model.setIsUpdate(Constant.UPDATW_YES);//已变更
        cargoInfoRepository.save(model);//修改旧数据

        CargoInfo newCargoInfo=new CargoInfo();
        newCargoInfo.setBrand(model.getBrand());
        newCargoInfo.setCargoCode(model.getCargoCode());
        newCargoInfo.setCargoName(model.getCargoName());
        newCargoInfo.setCargoSerial(model.getCargoSerial());
        newCargoInfo.setCreator(userName);
        newCargoInfo.setCreateDate(date);
        newCargoInfo.setCurrency(model.getCurrency());
        newCargoInfo.setGuaranteeRate(model.getGuaranteeRate());
        newCargoInfo.setIsDelete(model.getIsDelete());
        newCargoInfo.setIsUpdate(Constant.UPDATW_NO);
        newCargoInfo.setItemCode(model.getItemCode());
        newCargoInfo.setItemName(model.getItemName());
        newCargoInfo.setMainParams(cargoInfo.getMainParams());//主要参数
        newCargoInfo.setMaintenanceMan(userName);
        newCargoInfo.setMaintenanceDate(date);
        newCargoInfo.setManufactor(model.getManufactor());
        newCargoInfo.setModel(model.getModel());
        newCargoInfo.setRemark(model.getRemark());
        newCargoInfo.setStatus(8);//货物状态：1：草稿，2：审核中，3：同意，4退回，5完成，6结束，7建立项目，8已变更
        newCargoInfo.setType(model.getType());
        newCargoInfo.setPartnerId(model.getPartnerId());
        newCargoInfo.setReprice(model.getReprice());

        newCargoInfo.setOldCargoId(model.getOldCargoId());

        if(cargoInfo.getAttachment()!=null){
            newCargoInfo.setAttachment(cargoInfo.getAttachment());
        }
        CargoInfo cargoInfo1=new CargoInfo();

            Set<PartInfo> partInfos =cargoInfo.getPartInfos();
            if (partInfos != null && !partInfos.isEmpty()) {
                String partSerial = "0001";
                        //partInfoService.findLastPartSerial(model.getCargoSerial());
                int step = 0;
                for (PartInfo partInfo : partInfos) {
                    if (step == 0) {
                        partInfo.setPartSerial(Common.convertSerial(partSerial, 0));
                    } else {
                        partInfo.setPartSerial(Common.convertSerial(partSerial, 1));
                    }
                    partInfo.setPartCode(model.getCargoCode() + partInfo.getPartSerial());
                    partInfo.setIsDelete(Constant.DELETE_NO);
                    step++;
                }
                newCargoInfo.setPartInfos(partInfos);
                 cargoInfo1=cargoInfoRepository.save(newCargoInfo);
                List<Long> partIds = new ArrayList<>();
                if(partInfos.size()>0){
                    for (PartInfo partInfo : cargoInfo1.getPartInfos()) {
                        partIds.add(partInfo.getPartId());
                    }
                    partInfoRepository.setCargoId(cargoInfo1.getCargoId(), partIds);
                }
            }else {
                newCargoInfo.setPartInfos(model.getPartInfos());
                 cargoInfo1=cargoInfoRepository.save(newCargoInfo);
            }

        return cargoInfo1;
    }

    private String getStatus(int status){
        String name=null;
        if(status==1){
            name="待审核";
        }
        if(status==2){
            name="审核中";
        }
        if(status==3){
            name="审核中";
        }
        if(status==4){
            name="退回";
        }
        if(status==5){
            name="待审通过";
        }
        return name;
    }


    //货物导出
    public void export(HttpServletResponse response, List<Long> cargoIds,String actor) {
        try {
            String[] header = {"货物序号", "货物品目", "货物名称", "货物编号","状态", "品牌", "型号", "主要参数",
                    "产地", "进口/国产类别","参考价格", "币种", "维保率/月", "证明文件", "备注"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("货物列表");
            sheet.setDefaultColumnWidth(14);
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
            List<CargoInfo> list = null;
            if (cargoIds != null && !cargoIds.isEmpty()) {
                list = cargoInfoRepository.findByCargoName(cargoIds);
            } else if(null!=actor && cargoIds.isEmpty()){
                list = cargoInfoRepository.findAllExpert(Constant.DELETE_NO,actor);
            }else {
                list = cargoInfoRepository.findAll();
            }
            CargoInfo cargoInfo;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                DecimalFormat df = new DecimalFormat("0.00");
                cargoInfo = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                Attachment attachment=cargoInfo.getAttachment();
                if(attachment!=null){
                    attachment = attachmentRepository.getOne(cargoInfo.getAttachment().getAttachId());
                    row.createCell(13).setCellValue(new HSSFRichTextString(attachment.getAttachName()));
                }else {
                    row.createCell(13).setCellValue(new HSSFRichTextString(""));
                }
                row.createCell(0).setCellValue(new HSSFRichTextString(cargoInfo.getCargoSerial()));
                row.createCell(1).setCellValue(new HSSFRichTextString(cargoInfo.getItemName()));
                row.createCell(2).setCellValue(new HSSFRichTextString(cargoInfo.getCargoName()));
                row.createCell(3).setCellValue(new HSSFRichTextString(cargoInfo.getCargoCode()));
                row.createCell(4).setCellValue(new HSSFRichTextString(getStatus(cargoInfo.getStatus())));
                row.createCell(5).setCellValue(new HSSFRichTextString(cargoInfo.getBrand()));
                row.createCell(6).setCellValue(new HSSFRichTextString(cargoInfo.getModel()));
                row.createCell(7).setCellValue(new HSSFRichTextString(cargoInfo.getMainParams()));
                row.createCell(8).setCellValue(new HSSFRichTextString(cargoInfo.getManufactor()));
                row.createCell(9).setCellValue(new HSSFRichTextString(cargoInfo.getType()));
                row.createCell(10).setCellValue(new HSSFRichTextString(cargoInfo.getReprice()+""));
                row.createCell(11).setCellValue(new HSSFRichTextString(cargoInfo.getCurrency()));
                row.createCell(12).setCellValue(new HSSFRichTextString(cargoInfo.getGuaranteeRate()));
                if(cargoInfo.getRemark()!=null && !"".equals(cargoInfo.getRemark())){
                    row.createCell(14).setCellValue(new HSSFRichTextString(cargoInfo.getRemark()));
                }else {
                    row.createCell(14).setCellValue(new HSSFRichTextString(""));
                }
            }
            //response.setContentType("application/octet-stream");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=cargoInfo.xls");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
           // e.printStackTrace();
            logger.error("货物导出出现异常",e);

        }
    }

    //下载货物导入模板
    public void downloadByName(HttpServletResponse response) {
        try {
            String[] header = { "货物品目", "货物名称",  "品牌", "型号", "主要参数",
                    "产地", "进口/国产类别", "参考价格", "币种", "维保率/月", "备注"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("货物导入模板表");
            sheet.setDefaultColumnWidth(14);
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
            HSSFRow row = sheet.createRow( 1);
            row.createCell(0).setCellValue(new HSSFRichTextString());
            row.createCell(1).setCellValue(new HSSFRichTextString());
            row.createCell(2).setCellValue(new HSSFRichTextString());
            row.createCell(3).setCellValue(new HSSFRichTextString());
            row.createCell(4).setCellValue(new HSSFRichTextString());
            row.createCell(5).setCellValue(new HSSFRichTextString());
            row.createCell(6).setCellValue(new HSSFRichTextString());
            row.createCell(7).setCellValue(new HSSFRichTextString());
            row.createCell(8).setCellValue(new HSSFRichTextString());
            row.createCell(9).setCellValue(new HSSFRichTextString());
            row.createCell(10).setCellValue(new HSSFRichTextString());
            //response.setContentType("application/octet-stream");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=cargoInfoTemplate.xls");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("货物模板下载出现异常",e);
        }
    }

    private String findLastCargoSerial() {
        String serial = cargoInfoRepository.findLastCargoSerial();
        return Common.convertSerial(serial, 1);
    }

    private String findLastCargoSerial2(int i) {
        String serial = cargoInfoRepository.findLastCargoSerial();
        return Common.convertSerial(serial, 1+i);
    }

    public void upLoad(Attachment attachment ,JwtUser jwtUser,Long partnerId) {
        Map<String, Object> maps = new HashMap<String, Object>();
        try {
            //文件读取并插入数据库
            List list = new ArrayList();
            list = readCargoInfoExcelData(attachment.getPath());
            if (null == list || list.size() == 0) {
                //return StringUtil.getJsonString(true, 1, "导入数据为空!");
            }
            int num = list.size() / 200;
            if (list.size() % 200 != 0) {
                num++;
            }
            for (int i = 0; i < num; i++) {
                List tempList = list.subList(i * 200, (i + 1) * 200 > list.size() ? list.size() : (i + 1) * 200);
                batchInsertCargoInfo(tempList,jwtUser,partnerId);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("货物导入出现异常",e);
        }
    }
    public void batchInsertCargoInfo(List<Object> maps,JwtUser jwtUser,Long partnerId) {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        List<CargoInfo> listSave = new ArrayList<>();
        for (int i = 0; i < maps.size(); i++) {
            String jsonStr = maps.get(i).toString();
            JSONObject jsonObject = new JSONObject(jsonStr);
            CargoInfo cargoInfo = new CargoInfo();
            BrandItem brandItem=brandItemRepository.findByItemName(jsonObject.get("货物品目").toString());
            if(brandItem==null){
                cargoInfo.setItemCode(null);//品目code
                cargoInfo.setItemName(null);//品目name
            }else {
                cargoInfo.setItemCode(brandItem.getItemCode());//品目code
                cargoInfo.setItemName(brandItem.getItemName());//品目name
            }
            cargoInfo.setCargoSerial(this.findLastCargoSerial2(i));//货物序号
            cargoInfo.setCargoName(jsonObject.get("货物名称").toString());
            cargoInfo.setCargoCode(cargoInfo.getItemCode() + cargoInfo.getCargoSerial());//货物编号
            cargoInfo.setBrand(jsonObject.get("品牌").toString());
            cargoInfo.setModel(jsonObject.get("型号").toString());
            cargoInfo.setMainParams(jsonObject.get("主要参数").toString());
            cargoInfo.setManufactor(jsonObject.get("产地").toString());
            cargoInfo.setType(jsonObject.get("进口/国产类别").toString());
            cargoInfo.setReprice(Double.valueOf("".equals(jsonObject.get("参考价格").toString())?"0.00":jsonObject.get("参考价格").toString()));
            cargoInfo.setCurrency(jsonObject.get("币种").toString());
            cargoInfo.setGuaranteeRate(jsonObject.get("维保率/月").toString());
            cargoInfo.setRemark(jsonObject.get("备注").toString()!=null?jsonObject.get("备注").toString():"");
            cargoInfo.setStatus(1);//状态：草稿状态，审核中，审核完毕
            cargoInfo.setCreateDate(date);
            cargoInfo.setCreator(userName);
            cargoInfo.setMaintenanceDate(date);
            cargoInfo.setMaintenanceMan(userName);
            cargoInfo.setIsDelete(2);
            cargoInfo.setPartnerId(partnerId);//供应商id
            listSave.add(cargoInfo);
        }
        cargoInfoRepository.saveAll(listSave);
    }

    public static List<Object> readCargoInfoExcelData(String exclePath) {
        //构建JSONObject对象
        JSONObject json = null;
        Workbook wb = null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String, ExcelHeaderColumnPojo>> list = null;
        List<String> keys = null;
        String[] columns = {};// 存放Excel中的列名
        wb = readExcel(exclePath);// Excel文件读取
        if (wb != null) {
            list = new ArrayList<Map<String, ExcelHeaderColumnPojo>>();// 用来存放表中数据
            // 保存表头的字段名
            Map<String, Integer> nameMap = new HashMap<String, Integer>();
            List<Object> rowMap = new ArrayList<Object>();
            List<String> lists = new ArrayList<String>();
            for (int sheetnum = 0; sheetnum < wb.getNumberOfSheets(); sheetnum++) {
                sheet = wb.getSheetAt(sheetnum); // 获取第一个sheet
                int maxRow = sheet.getPhysicalNumberOfRows();// 获取最大行数
                keys = new ArrayList<String>();// 存放key
                row = sheet.getRow(0);// 获取第一行
                if (row == null) {
                    System.out.println("OperationExcel.main():Excel第一行表头为空!!");
                }
                int maxColumn = row.getPhysicalNumberOfCells(); // 获取最大列数
                columns = new String[maxColumn];
                for (int i = 0; i < maxColumn; i++) {// 遍历第一行数据,保存数据表头
                    columns[i] = (String) getCellFormatValue(row.getCell(i));
                    nameMap.put(columns[i], i);
                }
                String result = "";
                lists = readCargoInfoKey();
                // 提取数据
                for (int i = 1; i < maxRow; i++) {// 从第二行开始遍历所有行
                    json = new JSONObject();
                    row = sheet.getRow(i);// Excel中的第i行
                    if (row != null) {// 开始提取行内数据
                        for (int c = 0; c < lists.size(); c++) {
                            result = (String) getCellFormatValue(row.getCell(nameMap.get(lists.get(c))));
                            json.put(lists.get(c), result);
                        }
                        rowMap.add(json);
                    } else {
                        break;
                    }
                }
            }
            return rowMap;
        }

        return null;
    }
    /**
     * 读取Excel文件,返回workbook对象
     *
     * @param filePath
     * @return
     */
    public static Workbook readExcel(String filePath) {
        Workbook wb = null;
        if (filePath == null) {
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));// 判断Excel是什么版本的
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);// 文件流对象
            if (".xls".equals(extString)) {
                return wb = new HSSFWorkbook(is);// Excel版本2003
            } else {
                wb = null;
            }

        } catch (FileNotFoundException e) {
           // e.printStackTrace();
            logger.error("Workbook读取excel文件出现异常");
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("Workbook读取excel文件出现异常");
        }
        return wb;
    }

    /**
     * 获取表格中的数据
     *
     * @param cell
     * @return
     */
    public static Object getCellFormatValue(Cell cell) {
        Object cellValue = null;
        if (cell != null) {
            // 判断cell类型
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC: {
                    cellValue = String.valueOf((long) cell.getNumericCellValue());
                    break;
                }
                case Cell.CELL_TYPE_FORMULA: {
                    // 判断cell是否为日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 转换为日期格式YYYY-mm-dd
                        cellValue = cell.getDateCellValue();
                    } else {
                        // 数字
                        cellValue = cell.getNumericCellValue();
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING: {
                    cellValue = cell.getRichStringCellValue().getString();
                    break;
                }
                default:
                    cellValue = "";
            }
        } else {
            cellValue = "";
        }
        return cellValue;
    }

    public static List<String> readCargoInfoKey() {
        List<String> list = new ArrayList<>();
        list.add("货物品目");
        list.add("货物名称");
        list.add("品牌");
        list.add("型号");
        list.add("主要参数");
        list.add("产地");
        list.add("进口/国产类别");
        list.add("参考价格");
        list.add("币种");
        list.add("维保率/月");
        list.add("备注");
        return list;
    }

}
