package com.etone.protocolsupply.service.cargo;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.ExcelHeaderColumnPojo;
import com.etone.protocolsupply.model.dto.PartExpCollectionDto;
import com.etone.protocolsupply.model.dto.PartInfoExpDto;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import com.etone.protocolsupply.repository.project.PartInfoExpRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.cargo.PartInfoRepository;
import com.etone.protocolsupply.repository.project.ProjectInfoRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.json.JSONObject;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class PartInfoService {

    @Autowired
    private PartInfoRepository    partInfoRepository;
    @Autowired
    private PartInfoExpRepository partInfoExpRepository;
    @Autowired
    private CargoInfoRepository   cargoInfoRepository;
    @Autowired
    private ProjectInfoRepository projectInfoRepository;
    @Autowired
    private PagingMapper          pagingMapper;

    public PartInfo save(PartInfoDto partInfoDto) throws GlobalServiceException {
        if (Strings.isBlank(partInfoDto.getCargoId())) {
            throw new GlobalServiceException(GlobalExceptionCode.NOTNULL_ERROR.getCode(), GlobalExceptionCode.NOTNULL_ERROR.getCause("cargoId"));
        }
        PartInfo partInfo = new PartInfo();
        BeanUtils.copyProperties(partInfoDto, partInfo);
        //CargoInfo cargoInfo = cargoInfoRepository.findAllByCargoId(Long.parseLong(partInfoDto.getCargoId()));
        Optional<CargoInfo> optional = cargoInfoRepository.findById(Long.parseLong(partInfoDto.getCargoId()));
        if (optional.isPresent()) {
            partInfo.setCargoInfo(optional.get());
        }
        String cargoSerial = partInfo.getCargoInfo().getCargoSerial();
        String partSerial = partInfoRepository.findLastPartSerial(cargoSerial);
        partInfo.setPartSerial(Common.convertSerial(partSerial.toString(), 1));
        partInfo.setPartCode(partInfo.getCargoInfo().getCargoCode() + partInfo.getPartSerial());
        // partInfo.setCargoInfo(cargoInfo);
        partInfo.setIsDelete(Constant.DELETE_NO);
        return partInfoRepository.save(partInfo);
    }

    public Specification<PartInfo> getWhereClause(String isDelete, String cargoId, String projectId) {
        return (Specification<PartInfo>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(cargoId)) {
                predicates.add(criteriaBuilder.equal(root.get("cargoInfo").as(CargoInfo.class), cargoId));
            }
            if (Strings.isNotBlank(projectId)) {
                predicates.add(criteriaBuilder.equal(root.get("projectInfo").as(ProjectInfo.class), projectId));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public List<PartInfo> getWhereClause1(String isDelete, String partName, String manufactor) {
        return partInfoRepository.findAllBycon(isDelete, partName, manufactor);
    }


    //配件导出
    public Specification<PartInfo> getWhereClauseEx(String isDelete) {
        return (Specification<PartInfo>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<PartInfo> findPartInfos(String cargoId, String isDelete, Pageable pageable) {
        return Common.listConvertToPage(partInfoRepository.findAll(cargoId, isDelete), pageable);
    }

    //货物项目-配件列表
    public Page<PartInfoExp> findPartInfoExps(String projectId, String isDelete, Pageable pageable) {
        return Common.listConvertToPage(partInfoExpRepository.findAll(projectId, isDelete), pageable);
    }

    public PartCollectionDto to(Page<PartInfo> source, HttpServletRequest request) {
        PartCollectionDto partCollectionDto = new PartCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, partCollectionDto, request);
        PartInfoDto partInfoDto;
        for (PartInfo partInfo : source) {
            partInfoDto = new PartInfoDto();
            BeanUtils.copyProperties(partInfo, partInfoDto);
            partInfoDto.setCargoId(partInfo.getCargoInfo().getCargoId().toString());//货物id
            partInfoDto.setCargoName(partInfo.getCargoInfo().getCargoName());//货物名称
            partInfoDto.setCurrency(partInfo.getCargoInfo().getCurrency());//币种
            partInfoDto.setGuaranteeRate(partInfo.getCargoInfo().getGuaranteeRate());//维保率
            partCollectionDto.add(partInfoDto);
        }

        return partCollectionDto;
    }
    //货物项目-配件列表
    public PartExpCollectionDto toExp(Page<PartInfoExp> source, HttpServletRequest request) {
        PartExpCollectionDto partExpCollectionDto = new PartExpCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, partExpCollectionDto, request);
        PartInfoExpDto partInfoExpDto;
        for (PartInfoExp partInfoExp : source) {
            partInfoExpDto = new PartInfoExpDto();
            BeanUtils.copyProperties(partInfoExp, partInfoExpDto);
            partInfoExpDto.getCargoInfo().setPartInfos(null);
            partExpCollectionDto.add(partInfoExpDto);
            //partInfoExpDto.getProjectInfo().getInquiryInfo().getPartnerInfo().setContacts(null);
        }

        return partExpCollectionDto;
    }
    public PartInfo update(PartInfoDto partInfoDto) throws GlobalServiceException {
        PartInfo partInfo = partInfoRepository.findOneModel(partInfoDto.getPartId());
        partInfo.setPartName(partInfoDto.getPartName());
        partInfo.setStandards(partInfoDto.getStandards());
        partInfo.setTechParams(partInfoDto.getTechParams());
        partInfo.setManufactor(partInfoDto.getManufactor());
        partInfo.setUnit(partInfoDto.getUnit());
        partInfo.setQuantity(partInfoDto.getQuantity());
        partInfo.setPrice(partInfoDto.getPrice());
        partInfo.setRemark(partInfoDto.getRemark());
        return partInfoRepository.save(partInfo);
    }


    public void delete(List<Long> partIds) {
        partInfoRepository.updateIsDelete(partIds);
    }
    //删除货物项目-配件列表
    public void deleteExp(List<Long> partIds) {
        partInfoExpRepository.updateIsDelete(partIds);
    }

    //配件导入
    public void upLoad(Attachment attachment, String cargoId) {
        if (Strings.isBlank(cargoId)) {
            throw new GlobalServiceException(GlobalExceptionCode.NOTNULL_ERROR.getCode(), GlobalExceptionCode.NOTNULL_ERROR.getCause("cargoId"));
        }
        try {
            //文件读取并插入数据库
            List list = readPartInfoExcelData(attachment.getPath());
            if (null == list || list.size() == 0) {
                //return StringUtil.getJsonString(true, 1, "导入数据为空!");
            }
            int num = list.size() / 200;
            if (list.size() % 200 != 0) {
                num++;
            }
            for (int i = 0; i < num; i++) {
                List tempList = list.subList(i * 200, (i + 1) * 200 > list.size() ? list.size() : (i + 1) * 200);
                batchInsertPartInfo(tempList, cargoId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //配件导出
    public void export(HttpServletResponse response, Long cargoId, List<Long> partIds) {
        try {
            String[] header = {"配件编号", "设备或配件名称", "型号/规格", "产地/厂家", "主要技术参数", "单位", "数量",
                    "单价", "总价", "备注"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("配件列表");
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

            List<PartInfo> list = partInfoRepository.findAllBycargoId(cargoId,partIds);
            PartInfo partInfo;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                DecimalFormat df = new DecimalFormat("0.00");
                partInfo = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(partInfo.getPartCode()));
                row.createCell(1).setCellValue(new HSSFRichTextString(partInfo.getPartName()));
                row.createCell(2).setCellValue(new HSSFRichTextString(partInfo.getStandards()));
                row.createCell(3).setCellValue(new HSSFRichTextString(partInfo.getManufactor()));
                row.createCell(4).setCellValue(new HSSFRichTextString(partInfo.getTechParams()));
                row.createCell(5).setCellValue(new HSSFRichTextString(partInfo.getUnit()));
                row.createCell(6).setCellValue(new HSSFRichTextString(partInfo.getQuantity()));
                row.createCell(7).setCellValue(new HSSFRichTextString(partInfo.getPrice().toString()));
                row.createCell(8).setCellValue(new HSSFRichTextString(partInfo.getTotal().toString()));
                row.createCell(9).setCellValue(new HSSFRichTextString(partInfo.getRemark()));
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=partInfo.xls");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //项目-配件导出
    public void exportExp(HttpServletResponse response, List<Long> partIds) {
        try {
            String[] header = {"货物名称", "配件序号", "配件编号", "配件/设备名称", "型号/规格", "产地", "主要技术参数",
                    "单位", "数量", "备注"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("项目货物列表");
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

            List<PartInfoExp> list = partInfoExpRepository.findAllBypartIds(partIds);
            PartInfoExp partInfoExp;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                DecimalFormat df = new DecimalFormat("0.00");
                partInfoExp = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(partInfoExp.getCargoInfo().getCargoName()));
                row.createCell(1).setCellValue(new HSSFRichTextString(partInfoExp.getPartSerial()));
                row.createCell(2).setCellValue(new HSSFRichTextString(partInfoExp.getPartCode()));
                row.createCell(3).setCellValue(new HSSFRichTextString(partInfoExp.getPartName()));
                row.createCell(4).setCellValue(new HSSFRichTextString(partInfoExp.getStandards()));
                row.createCell(5).setCellValue(new HSSFRichTextString(partInfoExp.getManufactor()));
                row.createCell(6).setCellValue(new HSSFRichTextString(partInfoExp.getTechParams()));
                row.createCell(7).setCellValue(new HSSFRichTextString(partInfoExp.getUnit()));
                row.createCell(8).setCellValue(new HSSFRichTextString(partInfoExp.getQuantity()));
                row.createCell(9).setCellValue(new HSSFRichTextString(partInfoExp.getRemark()));
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=partInfoExp.xls");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<Object> readPartInfoExcelData(String exclePath) {
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
                lists = readPartInfoKey();
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    public static List<String> readPartInfoKey() {
        List<String> list = new ArrayList<>();
        //list.add("配件编号");
        list.add("设备或配件名称");
        list.add("型号/规格");
        list.add("产地/厂家");
        list.add("主要技术参数");
        list.add("单位");
        list.add("数量");
        list.add("单价");
        list.add("总价");
        list.add("备注");
        return list;
    }

    public void batchInsertPartInfo(List<Object> maps, String cargoId) {
        Long lcargoId = Long.parseLong(cargoId);
        List<PartInfo> partInfos = partInfoRepository.findAll();
        List<String> listDel = new ArrayList<>();
        List<PartInfo> listSave = new ArrayList<>();
        int num = 0;
        for (int i = 0; i < maps.size(); i++) {
            String jsonStr = maps.get(i).toString();
            JSONObject jsonObject = new JSONObject(jsonStr);
            PartInfo partInfo = new PartInfo();
            if (partInfos.size() > 0) {
                List<PartInfo> list = partInfoRepository.findAllBys(lcargoId);
                if (list.size() > 0) {
                    for (PartInfo item : list) {
                        if (item.getPartName().equals(jsonObject.get("设备或配件名称").toString())) {
                            listDel.add(item.getPartId().toString());
                        }
                    }
                }
            }
        }
        if (listDel.size() > 0) {
            partInfoRepository.deleteAll(listDel);
        }
        for (int i = 0; i < maps.size(); i++) {
            String jsonStr = maps.get(i).toString();
            JSONObject jsonObject = new JSONObject(jsonStr);
            PartInfo partInfo = new PartInfo();
            CargoInfo cargoInfo = cargoInfoRepository.findAllByCargoId(lcargoId);
            if (Strings.isBlank(partInfoRepository.findLastPartSerial(cargoInfo.getCargoSerial()))) {
                partInfo.setPartSerial("0001");
                num = 1;
            } else if (num == 1) {
                partInfo.setPartSerial(Common.convertSerial(partInfoRepository.findLastPartSerial(cargoInfo.getCargoSerial()), i));
            } else {
                partInfo.setPartSerial(Common.convertSerial(partInfoRepository.findLastPartSerial(cargoInfo.getCargoSerial()), i + 1));
            }
            partInfo.setPartCode(cargoInfo.getCargoCode() + partInfo.getPartSerial());
            partInfo.setPartName(jsonObject.get("设备或配件名称").toString());
            partInfo.setStandards(jsonObject.get("型号/规格").toString());
            partInfo.setManufactor(jsonObject.get("产地/厂家").toString());
            partInfo.setTechParams(jsonObject.get("主要技术参数").toString());
            partInfo.setUnit(jsonObject.get("单位").toString());
            partInfo.setQuantity(jsonObject.get("数量").toString());
            partInfo.setPrice(Double.valueOf("".equals(jsonObject.get("单价").toString())?"0.00":jsonObject.get("单价").toString()));
            partInfo.setTotal(Double.valueOf("".equals(jsonObject.get("总价").toString())?"0.00":jsonObject.get("总价").toString()));
            partInfo.setRemark(jsonObject.get("备注").toString());
            partInfo.setIsDelete(2);
            partInfo.setCargoInfo(cargoInfo);
            if (partInfo.getPartSerial().equals("0001")) {
                partInfoRepository.save(partInfo);
            } else {
                listSave.add(partInfo);
            }

        }

        partInfoRepository.saveAll(listSave);
    }

    public String findLastPartSerial(String categorySerial) {
        String serial = partInfoRepository.findLastPartSerial(categorySerial);
        return Common.convertSerial(serial, 1);
    }


}
