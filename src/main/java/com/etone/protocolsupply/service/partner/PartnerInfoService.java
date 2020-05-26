package com.etone.protocolsupply.service.partner;

import com.etone.protocolsupply.model.dto.partner.PartnerInfoCollectionDto;
import com.etone.protocolsupply.model.dto.partner.PartnerInfoDto;
import com.etone.protocolsupply.model.entity.supplier.BankInfo;
import com.etone.protocolsupply.model.entity.supplier.CertificateInfo;
import com.etone.protocolsupply.model.entity.supplier.ContactInfo;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.supplier.*;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Service
public class PartnerInfoService {

    @Autowired
    private PartnerInfoRepository partnerInfoRepository;

    @Autowired
    private CargoInfoRepository cargoInfoRepository;

    @Autowired
    private PagingMapper pagingMapper;

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Autowired
    private PartnerAccountRepository partnerAccountRepository;

    @Autowired
    private CertificateInfoRepository certificateInfoRepository;

    @Autowired
    private BankInfoRepository bankInfoRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private UserRepository userRepository;


    public Specification<PartnerInfo> getWhereClause(String isDelete, String supplierName) {
        return (Specification<PartnerInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(Strings.isNotBlank(supplierName)){
                predicates.add(criteriaBuilder.like(root.get("companyNo").as(String.class),"%"+supplierName+"%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), "2"));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }


    public PartnerInfoDto findByPartnerId(long partnerId) {
        PartnerInfo partnerInfo = partnerInfoRepository.findById(partnerId).get();
        PartnerInfoDto partnerInfoDto = new PartnerInfoDto();
        BeanUtils.copyProperties(partnerInfo,partnerInfoDto);

        List<ContactInfo> contactInfoList = contactInfoRepository.findByPartnerId(partnerId);

        CertificateInfo certificateInfo = certificateInfoRepository.findByPartnerId(partnerId);

        BankInfo bankInfo = bankInfoRepository.findByPartnerId(partnerId);

        User user = userRepository.findByPartnerId(partnerId);

        partnerInfoDto.setBankInfo(bankInfo);
        partnerInfoDto.setCertificateInfo(certificateInfo);
        partnerInfoDto.setContactInfoList(contactInfoList);
        partnerInfoDto.setCreditCode(user.getUsername());
        partnerInfoDto.setEmail(user.getEmail());
        partnerInfoDto.setTelephone(user.getTelephone());
        partnerInfoDto.setRealName(user.getFullname());
        return partnerInfoDto;
    }

    public boolean updatePartnerInfo(PartnerInfoDto partnerInfoDto) {

        PartnerInfo partnerInfo = new PartnerInfo();

        try {
            BeanUtils.copyProperties(partnerInfoDto,partnerInfo);

            //更新供应商基本信息
            partnerInfo = partnerInfoRepository.save(partnerInfo);

            //更新联系人,先删除相关表记录
            contactInfoRepository.deleteByPartnerId(partnerInfo.getPartnerId());

            if(partnerInfoDto.getContactInfoList()!=null && partnerInfoDto.getContactInfoList().size()>0){
                for (int i = 0; i < partnerInfoDto.getContactInfoList().size(); i++) {
                    ContactInfo save = contactInfoRepository.save(partnerInfoDto.getContactInfoList().get(i));
                    //更新partnerId
                    contactInfoRepository.updatePartnerId(partnerInfo.getPartnerId(),save.getContactId());
                }
            }

            //更新银行账户信息
            BankInfo bankInfo = bankInfoRepository.save(partnerInfoDto.getBankInfo());
            //更新银行附件信息
            bankInfoRepository.updateAttachId(partnerInfoDto.getBankInfo().getAttachment().getAttachId(),bankInfo.getBankId());


            //更新三证信息
            CertificateInfo certificateInfo = certificateInfoRepository.save(partnerInfoDto.getCertificateInfo());

            //更新三证中三个证件的附件信息
            certificateInfoRepository.updateAttachmentIds(partnerInfoDto.getCertificateInfo().getLicense().getAttachId(),
                    partnerInfoDto.getCertificateInfo().getIdentityCardFront().getAttachId(),
                    partnerInfoDto.getCertificateInfo().getIdentityCardBack().getAttachId(),
                    certificateInfo.getCertificateId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Page<PartnerInfo> findPartnerInfoList(String isDelete,String supplierName, Pageable pageable) {
        List<PartnerInfo> list = partnerInfoRepository.findByCondition("2", supplierName);
        return Common.listConvertToPage(list,pageable);
    }

    public PartnerInfoCollectionDto to(Page<PartnerInfo> page, HttpServletRequest request) {
        PartnerInfoCollectionDto partnerInfoCollectionDto = new PartnerInfoCollectionDto();
        pagingMapper.storeMappedInstanceBefore(page,partnerInfoCollectionDto,request);
        PartnerInfoDto partnerInfoDto;
        for(PartnerInfo partnerInfo:page){
            partnerInfoDto = new PartnerInfoDto();
            BeanUtils.copyProperties(partnerInfo,partnerInfoDto);
            partnerInfoCollectionDto.add(partnerInfoDto);
        }
        return partnerInfoCollectionDto;
    }

    public void export(HttpServletResponse response, List<Long> supplierIds) {
        try {
            String[] header = {"交易主体类型", "单位名称", "企业性质", "行业分类", "主营产品或业务", "注册资金", "注册资金币种", "注册资金单位", "企业人数",
            "法人代表姓名","证件号码","详细地址","机构注册地","公司所在地","邮政编码","公司网址","企业类别","公司简介","认证状态","认证方式","认证时间"};
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("我的供应商");
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

            List<PartnerInfo> list;
            if (supplierIds != null && !supplierIds.isEmpty()) {
                list = partnerInfoRepository.findAll(supplierIds);
            } else {
                list = partnerInfoRepository.findAll();
            }
            PartnerInfo partnerInfo;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                partnerInfo = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(new HSSFRichTextString(partnerInfo.getSupType()==1?"供应商":"代理商"));
                row.createCell(1).setCellValue(new HSSFRichTextString(partnerInfo.getCompanyNo()));
                row.createCell(2).setCellValue(new HSSFRichTextString(partnerInfo.getBusiNature()));
                row.createCell(3).setCellValue(new HSSFRichTextString(partnerInfo.getTradeCategory()));
                row.createCell(4).setCellValue(new HSSFRichTextString(partnerInfo.getBusiness()));
                row.createCell(5).setCellValue(new HSSFRichTextString(partnerInfo.getFund()));
                row.createCell(6).setCellValue(new HSSFRichTextString(partnerInfo.getFundCurrency()));
                row.createCell(7).setCellValue(new HSSFRichTextString(partnerInfo.getFundUnit()));
                row.createCell(8).setCellValue(new HSSFRichTextString(partnerInfo.getBusiNumber()));
                row.createCell(9).setCellValue(new HSSFRichTextString(partnerInfo.getIncorporator()));
                row.createCell(10).setCellValue(new HSSFRichTextString(partnerInfo.getIdentification()));
                row.createCell(11).setCellValue(new HSSFRichTextString(partnerInfo.getDetailAddress()));
                row.createCell(12).setCellValue(new HSSFRichTextString(partnerInfo.getDomicile()==1?"境内":"境外"));
                row.createCell(13).setCellValue(new HSSFRichTextString(partnerInfo.getAddress()));
                row.createCell(14).setCellValue(new HSSFRichTextString(partnerInfo.getZip()));
                row.createCell(15).setCellValue(new HSSFRichTextString(partnerInfo.getWebsite()));
                String busiType="";
                if(partnerInfo.getBusiType()==1){
                    busiType="监狱企业";
                }else if(partnerInfo.getBusiType()==2){
                    busiType="残疾人企业";
                }else {
                    busiType="中小微企业";
                }
                row.createCell(16).setCellValue(new HSSFRichTextString(busiType));
                row.createCell(17).setCellValue(new HSSFRichTextString(partnerInfo.getIntroduce()));
                row.createCell(18).setCellValue(new HSSFRichTextString(partnerInfo.getAuthStatus()==1?"已认证":"未认证"));
                row.createCell(19).setCellValue(new HSSFRichTextString(partnerInfo.getAuthMethod()));
                String authDate;
                if (partnerInfo.getAuthDate() == null) {
                    authDate = "";
                } else {
                    authDate = format.format(partnerInfo.getAuthDate());
                }
                row.createCell(20).setCellValue(new HSSFRichTextString(authDate));
            }
            response.setHeader("Content-disposition", "attachment;filename=supplierInfo.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
