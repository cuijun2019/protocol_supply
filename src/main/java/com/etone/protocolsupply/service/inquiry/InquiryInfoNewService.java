package com.etone.protocolsupply.service.inquiry;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoNewRepository;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional(rollbackFor = Exception.class)
@Service
public class InquiryInfoNewService {

    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private InquiryInfoNewRepository inquiryInfoNewRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private BusiJbpmFlowRepository busiJbpmFlowRepository;

    @Autowired
    private PagingMapper         pagingMapper;


    //新建保存
    public InquiryInfoNew save(InquiryInfoNewDto inquiryInfoNewDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        InquiryInfoNew inquiryInfoNew = new InquiryInfoNew();
        BeanUtils.copyProperties(inquiryInfoNewDto, inquiryInfoNew);
        String maxOneCode = inquiryInfoNewRepository.findMaxOne(inquiryInfoNew.getCargoInfo().getCargoId().toString());
        if (maxOneCode == null) {
            inquiryInfoNew.setInquiryCode("XJD-" + Common.getYYYYMMDDDate(date) + "-001");
        } else {
            inquiryInfoNew.setInquiryCode("XJD-" + Common.getYYYYMMDDDate(date) + "-" + Common.convertSerialProject(maxOneCode.substring(13), 1));

        }
        inquiryInfoNew.setCreator(userName);//创建人
        inquiryInfoNew.setCreateDate(date);//创建时间
        inquiryInfoNew.setIsDelete(Constant.DELETE_NO);
        //以下货物信息关联询价表有待思考
        CargoInfo cargoInfo = inquiryInfoNewDto.getCargoInfo();
        if (cargoInfo != null && cargoInfo.getCargoId()!=null) {
            Optional<CargoInfo> optional = cargoInfoRepository.findById(cargoInfo.getCargoId());
            if (optional.isPresent()) {
                inquiryInfoNew.setCargoInfo(optional.get());
            }
        }else {
            inquiryInfoNew.setCargoInfo(null);
        }

        inquiryInfoNewRepository.save(inquiryInfoNew);
        inquiryInfoNew.getCargoInfo().setPartInfos(null);//如果暂时setnull就会error："Could not write JSON: Infinite recursion (StackOverflowError); nested exception is com.fasterxml.jackson.databind.JsonMappingException: Infinite recursion (StackOverflowError) (through reference chain: com.etone.protocolsupply.model.dto.ResponseValue[\"data\"]->com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew[\"cargoInfo\"]->com.etone.protocolsupply.model.entity.cargo.CargoInfo[\"partInfos\"])"
        return inquiryInfoNew;
    }

    //查询list
    public Page<InquiryInfoNew> findInquiryInfoNewList(String isDelete, String inquiryCode,String cargoName,String actor,Integer status, Pageable pageable) {
        //查询全部询价
        if(null == actor || actor.equals("admin")){
            return Common.listConvertToPage(inquiryInfoNewRepository.findAllList(isDelete,inquiryCode,cargoName,status), pageable);
        }else {
            //根据不同的创建人查询询价
            return Common.listConvertToPage(inquiryInfoNewRepository.findAllListWithCreator(isDelete, inquiryCode,cargoName,status,actor), pageable);
        }
    }

    //对查询得到的list进行处理
    public InquiryInfoNewCollectionDto to(Page<InquiryInfoNew> source, HttpServletRequest request) {
        InquiryInfoNewCollectionDto inquiryInfoNewCollectionDto = new InquiryInfoNewCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, inquiryInfoNewCollectionDto, request);
        InquiryInfoNewDto inquiryInfoNewDto;
        for (InquiryInfoNew inquiryInfoNew : source) {
            inquiryInfoNewDto = new InquiryInfoNewDto();
            //必须要setnull，不然会error，Could not write JSON
            inquiryInfoNew.getCargoInfo().setPartInfos(null);
            BeanUtils.copyProperties(inquiryInfoNew, inquiryInfoNewDto);
            inquiryInfoNewCollectionDto.add(inquiryInfoNewDto);
        }
        return inquiryInfoNewCollectionDto;
    }

    //根据询价id查询询价详情
    public InquiryInfoNew findOne(Long inquiryId) {
        Optional<InquiryInfoNew> optional = inquiryInfoNewRepository.findById(inquiryId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    //查询是否发送过询价记录
    public BusiJbpmFlow sffsxjjl(String businessId,String businessType,String type,String actor){
        return busiJbpmFlowRepository.findsffsxjjl(businessId,businessType,type,actor);

    }

    //询价修改
    public InquiryInfoNew update(InquiryInfoNewDto inquiryInfoNewDto) throws GlobalServiceException{
        InquiryInfoNew inquiryInfoNew = new InquiryInfoNew();
        BeanUtils.copyProperties(inquiryInfoNewDto, inquiryInfoNew);
        if(null!=inquiryInfoNewDto.getInquiryId() && !"".equals(inquiryInfoNewDto.getInquiryId())){
            //inquiryInfoNew.setInquiryId(inquiryInfoNewDto.getInquiryId());
            inquiryInfoNew=inquiryInfoNewRepository.findAllByInquiryId(inquiryInfoNewDto.getInquiryId());
            inquiryInfoNew.setProjectBudget(inquiryInfoNewDto.getProjectBudget()==null?inquiryInfoNew.getProjectBudget():inquiryInfoNewDto.getProjectBudget());
            inquiryInfoNew.setProjectBackground(inquiryInfoNewDto.getProjectBackground()==null?inquiryInfoNew.getProjectBackground():inquiryInfoNewDto.getProjectBackground());
            inquiryInfoNew.setPurchaser(inquiryInfoNewDto.getPurchaser()==null?inquiryInfoNew.getPurchaser():inquiryInfoNewDto.getPurchaser());
            inquiryInfoNew.setManufactor(inquiryInfoNewDto.getManufactor()==null?inquiryInfoNew.getManufactor():inquiryInfoNewDto.getManufactor());
            inquiryInfoNew.setStatus(inquiryInfoNewDto.getStatus()==null?inquiryInfoNew.getStatus():inquiryInfoNewDto.getStatus());
            //
            inquiryInfoNew.setProjectId(inquiryInfoNewDto.getProjectId()==null?inquiryInfoNew.getProjectId():inquiryInfoNewDto.getProjectId());
        }
        CargoInfo cargoInfo = inquiryInfoNewDto.getCargoInfo();
        Attachment attachment = inquiryInfoNewDto.getAttachment();
        if (attachment != null && attachment.getAttachId()!=null && !attachment.getAttachId().equals("")) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                inquiryInfoNew.setAttachment(optional.get());
            }
        }
        if (cargoInfo != null && cargoInfo.getCargoId()!=null && !cargoInfo.getCargoId().equals("")) {
            Optional<CargoInfo> optional = cargoInfoRepository.findById(cargoInfo.getCargoId());
            if (optional.isPresent()) {
                inquiryInfoNew.setCargoInfo(optional.get());

            }
        }

        inquiryInfoNewRepository.save(inquiryInfoNew);
        inquiryInfoNew.getCargoInfo().setPartInfos(null);
        return inquiryInfoNew;
    }


    //批量删除
    public void delete(List<Long> inquiryIds) {
        inquiryInfoNewRepository.updateIsDelete(inquiryIds);
    }

    //询价导出
    public void export(HttpServletResponse response, List<Long> inquiryIds, String actor) {
        try {
            String[] header = {"询价单号", "采购人", "厂家名称", "货物名称", "项目预算"};
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
            List<InquiryInfoNew> list = null;
            if (inquiryIds != null && !inquiryIds.isEmpty()) {
                list = inquiryInfoNewRepository.findByInquiryIds(inquiryIds);
            } else if(null != actor && inquiryIds==null){
                list = inquiryInfoNewRepository.findExpert(Constant.DELETE_NO,actor);
            }else {
                list = inquiryInfoNewRepository.findAll();
            }
            InquiryInfoNew inquiryInfoNew;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < list.size(); i++) {
                DecimalFormat df = new DecimalFormat("0.00");
                inquiryInfoNew = list.get(i);
                HSSFRow row = sheet.createRow(i + 1);

                CargoInfo cargoInfo=inquiryInfoNew.getCargoInfo();//货物
                if(cargoInfo!=null){
                    cargoInfo = cargoInfoRepository.getOne(inquiryInfoNew.getCargoInfo().getCargoId());
                    row.createCell(3).setCellValue(new HSSFRichTextString(cargoInfo.getCargoName()));//货物名称
                }else {
                    row.createCell(3).setCellValue(new HSSFRichTextString(""));
                }
                row.createCell(0).setCellValue(new HSSFRichTextString(inquiryInfoNew.getInquiryCode()));
                row.createCell(1).setCellValue(new HSSFRichTextString(inquiryInfoNew.getPurchaser()));
                row.createCell(2).setCellValue(new HSSFRichTextString(inquiryInfoNew.getManufactor()));
                row.createCell(4).setCellValue(new HSSFRichTextString(inquiryInfoNew.getProjectBudget()+""));
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=inquiryInfo.xls");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<BusiJbpmFlow> getSetBusiJbpmFlowList(Long businessId,String businessType){
        Set<BusiJbpmFlow> busiJbpmFlows=busiJbpmFlowRepository.getSetBusiJbpmFlowList(businessId,businessType);
        return busiJbpmFlows;
    }


}
