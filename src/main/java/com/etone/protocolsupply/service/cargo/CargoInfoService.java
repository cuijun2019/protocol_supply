package com.etone.protocolsupply.service.cargo;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.ExcelHeaderColumnPojo;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.cargo.CargoCollectionDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.CargoInfo;
import com.etone.protocolsupply.model.entity.PartInfo;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.CargoInfoRepository;
import com.etone.protocolsupply.repository.PartInfoRepository;
import com.etone.protocolsupply.utils.PagingMapper;
import com.etone.protocolsupply.utils.SpringUtil;
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
public class CargoInfoService {

    @Autowired
    private PartInfoRepository partInfoRepository;
    @Autowired
    private CargoInfoRepository cargoInfoRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private PagingMapper       pagingMapper;

    public CargoInfo save(CargoInfo cargoInfo, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        cargoInfo.setIsDelete(Constant.DELETE_NO);
        cargoInfo.setCreator(userName);
        cargoInfo.setCreateDate(date);
        cargoInfo.setMaintenanceDate(date);
        cargoInfo.setMaintenanceMan(userName);
        Attachment attachment = cargoInfo.getAttachment();
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                cargoInfo.setAttachment(optional.get());
            }
        }
//        cargoInfo.getAttachment().setAttachId(cargoInfoDto.getAttachment().getAttachId());
        //cargoInfo.setCargoSerial();//序号
        //cargoInfo.setCargoCode();//编号
        return cargoInfoRepository.save(cargoInfo);
    }

    public Specification<CargoInfo> getWhereClause(String isDelete,String cargoName) {
        return (Specification<CargoInfo>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(cargoName)) {
                predicates.add(criteriaBuilder.like(root.get("cargoName").as(String.class), "%"+cargoName+"%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };


    }


    public Page<CargoInfo> findCargoInfos(Specification<CargoInfo> specification, Pageable pageable) {
        return cargoInfoRepository.findAll(specification, pageable);
    }

    public CargoCollectionDto to(Page<CargoInfo> source, HttpServletRequest request) {
        CargoCollectionDto cargoCollectionDto = new CargoCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, cargoCollectionDto, request);
        CargoInfoDto cargoInfoDto;
        for (CargoInfo cargoInfo : source) {
            cargoInfoDto = new CargoInfoDto();
            BeanUtils.copyProperties(cargoInfo, cargoInfoDto);
            cargoCollectionDto.add(cargoInfoDto);
        }
        return cargoCollectionDto;
    }

    public void delete(Long cargoId) {
        cargoInfoRepository.updateIsDelete(cargoId);
    }

    public CargoInfo findOne(Long cargoId) {
        CargoInfo cargoInfo= cargoInfoRepository.findAllByCargoId(cargoId);
        return cargoInfo;
    }
    public CargoInfo update(CargoInfo cargoInfo,JwtUser jwtUser) throws GlobalServiceException {
       // CargoInfo cargoInfo = this.findOne(cargoInfoDto.getCargoId());
        Date date = new Date();
        String userName = jwtUser.getUsername();

        cargoInfo.setMaintenanceMan(userName);
        cargoInfo.setMaintenanceDate(date);
        Optional<Attachment> attachment = attachmentRepository.findById(cargoInfo.getAttachment().getAttachId());

        if (cargoInfo != null && attachment == null) {
            cargoInfoRepository.save(cargoInfo);
        }
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(cargoInfo.getAttachment().getAttachId());
            if (optional.isPresent()) {
                cargoInfo.setAttachment(optional.get());
            }
            cargoInfoRepository.save(cargoInfo);
        }
        return cargoInfo;
    }


    //配件导出
    public void export(HttpServletResponse response, String cargoName) {
        try {
            String[] header = {"货物序号", "货物品目", "货物名称", "货物编号", "品牌", "型号", "主要参数",
                    "产地", "进口/国产类别", "币种","*维保率/月","证明文件","备注"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("货物列表");
            sheet.setDefaultColumnWidth(13);
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
            List<CargoInfo> list=null;
            if(  cargoName!=null &&!cargoName.equals("")){
                 list = cargoInfoRepository.findByCargoName(cargoName);
            }else {
                 list = cargoInfoRepository.findAll();
            }
            CargoInfo cargoInfo;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                DecimalFormat df = new DecimalFormat("0.00");
                cargoInfo = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                Attachment attachment =attachmentRepository.getOne(cargoInfo.getAttachment().getAttachId());
                row.createCell(0).setCellValue(new HSSFRichTextString(cargoInfo.getCargoSerial()));
                row.createCell(1).setCellValue(new HSSFRichTextString(cargoInfo.getCargoCategory()));
                row.createCell(2).setCellValue(new HSSFRichTextString(cargoInfo.getCargoName()));
                row.createCell(3).setCellValue(new HSSFRichTextString(cargoInfo.getCargoCode()));
                row.createCell(4).setCellValue(new HSSFRichTextString(cargoInfo.getBrand()));
                row.createCell(5).setCellValue(new HSSFRichTextString(cargoInfo.getModel()));
                row.createCell(6).setCellValue(new HSSFRichTextString(cargoInfo.getMainParams()));
                row.createCell(7).setCellValue(new HSSFRichTextString(cargoInfo.getManufactor()));
                row.createCell(8).setCellValue(new HSSFRichTextString(cargoInfo.getType()));
                row.createCell(9).setCellValue(new HSSFRichTextString(cargoInfo.getCurrency()));
                row.createCell(10).setCellValue(new HSSFRichTextString(cargoInfo.getGuaranteeRate()));
                row.createCell(11).setCellValue(new HSSFRichTextString(attachment.getAttachName()));
                row.createCell(12).setCellValue(new HSSFRichTextString(cargoInfo.getRemark()));
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=cargoInfo.xls");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
