package com.etone.protocolsupply.service;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.ExcelHeaderColumnPojo;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.CargoInfo;
import com.etone.protocolsupply.model.entity.PartInfo;
import com.etone.protocolsupply.repository.CargoInfoRepository;
import com.etone.protocolsupply.repository.PartInfoRepository;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class PartInfoService {

    @Autowired
    private PartInfoRepository partInfoRepository;
    @Autowired
    private CargoInfoRepository cargoInfoRepository;
    @Autowired
    private PagingMapper       pagingMapper;

    public void save(PartInfoDto partInfoDto) throws GlobalServiceException {
        PartInfo partInfo = new PartInfo();
        BeanUtils.copyProperties(partInfoDto, partInfo);
        partInfo.setIsDelete(Constant.DELETE_NO);
        partInfoRepository.save(partInfo);
    }

    public Specification<PartInfo> getWhereClause(String isDelete) {
        return (Specification<PartInfo>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    //配件导出
    public Specification<PartInfo> getWhereClauseEx(String cargoId, String isDelete) {
        return (Specification<PartInfo>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(cargoId)) {
                predicates.add(criteriaBuilder.equal(root.get("cargoId").as(String.class), cargoId));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<PartInfo> findPartInfos(Specification<PartInfo> specification, Pageable pageable) {
        return partInfoRepository.findAll(specification, pageable);
    }

    public PartCollectionDto to(Page<PartInfo> source, HttpServletRequest request) {
        PartCollectionDto partCollectionDto = new PartCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, partCollectionDto, request);
        PartInfoDto partInfoDto;
        for (PartInfo partInfo : source) {
            partInfoDto = new PartInfoDto();
            BeanUtils.copyProperties(partInfo, partInfoDto);
            partCollectionDto.add(partInfoDto);
        }
        return partCollectionDto;
    }

    public void delete(Long partId) {
        partInfoRepository.updateIsDelete(partId);
    }

    //配件导入
    public void upLoad(Attachment attachment, String cargoId) {
        Map<String,Object> maps = new HashMap<String,Object>();
        try{
                //文件读取并插入数据库
                List list = new ArrayList();
                list =readPartInfoExcelData(attachment.getPath());
                if (null == list || list.size() == 0) {
                    //return StringUtil.getJsonString(true, 1, "导入数据为空!");
                }
                int num = list.size() / 200;
                if (list.size() % 200 != 0) {
                    num++;
                }
                for (int i = 0; i < num; i++) {
                    List tempList = list.subList(i * 200, (i + 1) * 200 > list.size() ? list.size() : (i + 1) * 200);
                    batchInsertPartInfo(tempList,cargoId);
                }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //配件导出
    public void export(HttpServletResponse response, Long cargoId) {
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

            List<PartInfo> list = partInfoRepository.findAllBycargoId(cargoId);
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


    public static List<Object> readPartInfoExcelData(String exclePath) {
        //构建JSONObject对象
        JSONObject json = null;
        Workbook wb = null;
        Sheet sheet = null;
        Row row = null;
        List<Map<String, ExcelHeaderColumnPojo>> list = null;
        List<String> keys=null;
        String[] columns = {};// 存放Excel中的列名
        wb = readExcel(exclePath);// Excel文件读取
        if (wb != null) {
            list = new ArrayList<Map<String, ExcelHeaderColumnPojo>>();// 用来存放表中数据
            // 保存表头的字段名
            Map<String, Integer> nameMap = new HashMap<String, Integer>();
            List<Object> rowMap = new ArrayList<Object>();
            List<String> lists = new ArrayList<String>();
            for(int sheetnum=0;sheetnum<wb.getNumberOfSheets();sheetnum++){
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
                String result="";
                lists = readPartInfoKey();
                // 提取数据
                for (int i = 1; i < maxRow; i++) {// 从第二行开始遍历所有行
                    json = new JSONObject();
                    row = sheet.getRow(i);// Excel中的第i行
                    if (row != null) {// 开始提取行内数据
                        for (int c=0;c<lists.size();c++){
                            result = (String) getCellFormatValue(row.getCell(nameMap.get(lists.get(c))));
                            json.put(lists.get(c),result);
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
        list.add("配件编号");
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

    public void batchInsertPartInfo(List<Object> maps,String cargoId) {
        Long lcargoId=Long.parseLong(cargoId);
        Specification<PartInfo> specification = getWhereClause("2");
        List<PartInfo> list=partInfoRepository.findAll(specification);
        for (int i=0;i<maps.size();i++) {
            String jsonStr=maps.get(i).toString();
            JSONObject jsonObject = new JSONObject(jsonStr);
            PartInfo partInfo=new PartInfo();
            for(PartInfo item:list){
                if(item.getPartCode().equals(jsonObject.get("配件编号").toString())){
                    partInfoRepository.deleteByPartId(item.getPartId());
                }
            }

            partInfo.setPartCode(jsonObject.get("配件编号").toString());
            partInfo.setPartName(jsonObject.get("设备或配件名称").toString());
            partInfo.setStandards(jsonObject.get("型号/规格").toString());
            partInfo.setManufactor(jsonObject.get("产地/厂家").toString());
            partInfo.setTechParams(jsonObject.get("主要技术参数").toString());
            partInfo.setUnit(jsonObject.get("单位").toString());
            partInfo.setQuantity(jsonObject.get("数量").toString());
            partInfo.setPrice(Double.parseDouble(jsonObject.get("单价").toString()));
            partInfo.setTotal(Double.parseDouble(jsonObject.get("总价").toString()));
            partInfo.setRemark(jsonObject.get("备注").toString());
            partInfo.setIsDelete(2);
            CargoInfo cargoInfo=cargoInfoRepository.findAllByCargoId(lcargoId);
            partInfo.setCargoInfo(cargoInfo);
            partInfoRepository.save(partInfo);
        }

    }
}
