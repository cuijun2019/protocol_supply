package com.etone.protocolsupply.service.inquiry;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.ExcelHeaderColumnPojo;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.cargo.CargoCollectionDto;
import com.etone.protocolsupply.model.dto.cargo.CargoInfoDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.BrandItem;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.BrandItemRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.cargo.PartInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import com.etone.protocolsupply.service.cargo.PartInfoService;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
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
public class InquiryInfoService {

    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private InquiryInfoRepository inquiryInfoRepository;
    @Autowired
    private PartnerInfoRepository partnerInfoRepository;
    @Autowired
    private PartInfoService      partInfoService;
    @Autowired
    private PartInfoRepository   partInfoRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private PagingMapper         pagingMapper;

    @Autowired
    private BrandItemRepository brandItemRepository;

    public InquiryInfo save(InquiryInfoDto inquiryInfoDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        InquiryInfo inquiryInfo = new InquiryInfo();
        BeanUtils.copyProperties(inquiryInfoDto, inquiryInfo);
        inquiryInfo.setInquiryCode("xjc-");//未定义
        inquiryInfo.setInquiryDate(date);//询价时间
        inquiryInfo.setIsDelete(Constant.DELETE_NO);
        inquiryInfo.setStatus(1);//询价状态
        CargoInfo cargoInfo = inquiryInfoDto.getCargoInfo();
        if (cargoInfo != null && cargoInfo.getCargoId()!=null && !cargoInfo.getCargoId().equals("")) {
            Optional<CargoInfo> optional = cargoInfoRepository.findById(cargoInfo.getCargoId());
            if (optional.isPresent()) {
                inquiryInfo.setCargoInfo(optional.get());
            }
        }else {
            inquiryInfo.setCargoInfo(null);
        }
        PartnerInfo partnerInfo = inquiryInfoDto.getPartnerInfo();
        if (partnerInfo != null && partnerInfo.getPartnerId()!=null && !partnerInfo.getPartnerId().equals("")) {
            Optional<PartnerInfo> optional = partnerInfoRepository.findById(partnerInfo.getPartnerId());
            if (optional.isPresent()) {
                inquiryInfo.setPartnerInfo(optional.get());
            }
        }else {
            inquiryInfo.setPartnerInfo(null);
        }
        inquiryInfo = inquiryInfoRepository.save(inquiryInfo);
        return inquiryInfo;
    }


    public Page<InquiryInfo> findInquiryInfos(String isDelete, String cargoName, String inquiryCode, Pageable pageable) {
        return Common.listConvertToPage(inquiryInfoRepository.findAll(isDelete, cargoName, inquiryCode), pageable);
    }

    public InquiryCollectionDto to(Page<InquiryInfo> source, HttpServletRequest request) {
        InquiryCollectionDto inquiryCollectionDto = new InquiryCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, inquiryCollectionDto, request);
        InquiryInfoDto inquiryInfoDto;
        for (InquiryInfo inquiryInfo : source) {
            inquiryInfoDto = new InquiryInfoDto();
            BeanUtils.copyProperties(inquiryInfo, inquiryInfoDto);
            inquiryCollectionDto.add(inquiryInfoDto);
        }
        return inquiryCollectionDto;
    }

    public InquiryInfo findOne(Long inquiryId) {
        Optional<InquiryInfo> optional = inquiryInfoRepository.findById(inquiryId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过询价id"));
        }
    }

    public void delete(Long inquiryId) {
        inquiryInfoRepository.updateIsDelete(inquiryId);
    }


    //货物导出
    public void export(HttpServletResponse response, List<Long> inquiryIds) {
        try {
            String[] header = {"询价单号", "采购人", "采购单位", "供应商名称","货物基本参数", "货物名称", "参考价格", "询价时间",
                    "备注"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("询价列表");
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
            List<InquiryInfo> list = null;
            if (inquiryIds != null && !inquiryIds.equals("")) {
                list = inquiryInfoRepository.findByInquiryIds(inquiryIds);
            } else {
                list = inquiryInfoRepository.findAll();
            }
            InquiryInfo inquiryInfo;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                DecimalFormat df = new DecimalFormat("0.00");
                inquiryInfo = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                PartnerInfo partnerInfo=inquiryInfo.getPartnerInfo();//供应商
                if(partnerInfo!=null){
                    partnerInfo = partnerInfoRepository.getOne(inquiryInfo.getPartnerInfo().getPartnerId());
                    row.createCell(3).setCellValue(new HSSFRichTextString(partnerInfo.getCompanyNo()));//供应商名称
                }else {
                    row.createCell(3).setCellValue(new HSSFRichTextString(""));
                }
                CargoInfo cargoInfo=inquiryInfo.getCargoInfo();//货物
                if(cargoInfo!=null){
                    cargoInfo = cargoInfoRepository.getOne(inquiryInfo.getCargoInfo().getCargoId());
                    row.createCell(5).setCellValue(new HSSFRichTextString(cargoInfo.getCargoName()));//货物名称
                }else {
                    row.createCell(5).setCellValue(new HSSFRichTextString(""));
                }
                row.createCell(0).setCellValue(new HSSFRichTextString(inquiryInfo.getInquiryCode()));
                row.createCell(1).setCellValue(new HSSFRichTextString(inquiryInfo.getPurchaser()));
                row.createCell(2).setCellValue(new HSSFRichTextString(inquiryInfo.getUnit()));
                row.createCell(4).setCellValue(new HSSFRichTextString(inquiryInfo.getCargoBaseInfo()));
                row.createCell(6).setCellValue(new HSSFRichTextString(inquiryInfo.getRePrice().toString()));
                row.createCell(7).setCellValue(new HSSFRichTextString(inquiryInfo.getInquiryDate().toString()));
                row.createCell(8).setCellValue(new HSSFRichTextString(inquiryInfo.getRemark()));
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=inquiryInfo.xls");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
