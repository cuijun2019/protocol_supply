package com.etone.protocolsupply.service.inquiry;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;

import com.etone.protocolsupply.model.entity.user.Leaders;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoNewRepository;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
import com.etone.protocolsupply.repository.user.LeadersRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(InquiryInfoNewService.class);

    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private InquiryInfoNewRepository inquiryInfoNewRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private BusiJbpmFlowRepository busiJbpmFlowRepository;
    @Autowired
    private LeadersRepository leadersRepository;

    @Autowired
    private PagingMapper         pagingMapper;
    @Autowired
    private UserRepository userRepository;


    //新建保存
    public InquiryInfoNew save(InquiryInfoNewDto inquiryInfoNewDto, JwtUser jwtUser) throws GlobalServiceException {

        Date date = new Date();
        String userName = jwtUser.getUsername();
        InquiryInfoNew inquiryInfoNew = new InquiryInfoNew();
        BeanUtils.copyProperties(inquiryInfoNewDto, inquiryInfoNew);
        String maxOneCode = inquiryInfoNewRepository.findMaxOne();
        if (maxOneCode == null) {
            inquiryInfoNew.setInquiryCode("XJD-" + Common.getYYYYMMDDDate(date) + "-001");
        } else {
            inquiryInfoNew.setInquiryCode("XJD-" + Common.getYYYYMMDDDate(date) + "-" + Common.convertSerialProject(maxOneCode.substring(13), 1));

        }
        inquiryInfoNew.setCreator(userName);//创建人
        inquiryInfoNew.setCreateDate(date);//创建时间
        inquiryInfoNew.setIsDelete(Constant.DELETE_NO);
        //以下产品信息关联询价表有待思考
        //CargoInfo cargoInfo = inquiryInfoNewDto.getCargoInfo();
        if (inquiryInfoNewDto.getCargoInfo().getCargoId()!=null) {
            Optional<CargoInfo> optional = cargoInfoRepository.findById(inquiryInfoNewDto.getCargoInfo().getCargoId());
            if (optional.isPresent()) {
                inquiryInfoNew.setCargoInfo(optional.get());
            }else {
                throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("询价带入的产品id"));
            }
        }else {
            inquiryInfoNew.setCargoInfo(null);
        }
        inquiryInfoNewRepository.save(inquiryInfoNew);
        inquiryInfoNew.getCargoInfo().setPartInfos(null);//暂时setnull,否则就会error："Could not write JSON: Infinite recursion (StackOverflowError); nested exception is com.fasterxml.jackson.databind.JsonMappingException: Infinite recursion (StackOverflowError) (through reference chain: com.etone.protocolsupply.model.dto.ResponseValue[\"data\"]->com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew[\"cargoInfo\"]->com.etone.protocolsupply.model.entity.cargo.CargoInfo[\"partInfos\"])"
        return inquiryInfoNew;
    }
    public Page<InquiryInfoNew> getProjectInfoBudget(String fundsCardNumber, String itemName, Pageable pageable) {
        return Common.listConvertToPage(inquiryInfoNewRepository.getProjectInfoBudget(fundsCardNumber,itemName), pageable);
    }
    public InquiryInfoNewCollectionDto getProjectInfoBudgetto(Page<InquiryInfoNew> source,String itemName, HttpServletRequest request) {
        InquiryInfoNewCollectionDto inquiryInfoNewCollectionDto = new InquiryInfoNewCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, inquiryInfoNewCollectionDto, request);
        InquiryInfoNewDto inquiryInfoNewDto;
        double sum=0;
        for (InquiryInfoNew inquiryInfoNew: source) {
            sum+=inquiryInfoNew.getProjectBudget();
            inquiryInfoNewDto=new InquiryInfoNewDto();
            inquiryInfoNew.getCargoInfo().setPartInfos(null);
            BeanUtils.copyProperties(inquiryInfoNew, inquiryInfoNewDto);
            inquiryInfoNewDto.setItemName(itemName);
            inquiryInfoNewDto.setCargoInfo(null);
            inquiryInfoNewDto.setAttachment(null);
            inquiryInfoNewCollectionDto.add(inquiryInfoNewDto);

        }
        if(sum>1000000){
            for(InquiryInfoNewDto inquiryInfoNewDto1: inquiryInfoNewCollectionDto.getInquiryInfoNewDtos()){
                inquiryInfoNewDto1.setSum(sum);
                inquiryInfoNewDto1.setSfcgybw("1");
            }
        }else {
            for(InquiryInfoNewDto inquiryInfoNewDto2: inquiryInfoNewCollectionDto.getInquiryInfoNewDtos()){
                inquiryInfoNewDto2.setSum(sum);
                inquiryInfoNewDto2.setSfcgybw("0");
            }
        }

        return inquiryInfoNewCollectionDto;
    }



    //查询list
    public Page<InquiryInfoNew> findInquiryInfoNewList(String isDelete, String inquiryCode,String cargoName,JwtUser actor,Integer status, Pageable pageable) {
        //判断当前用户是什么角色，如果是招标中心经办人或者招标科长或者admin则查询全部采购通知书
        Long roleId = userRepository.findRoleIdByUsername(actor.getUsername());
        if( "5".equals(roleId+"") || "6".equals(roleId+"")|| "7".equals(roleId+"")){
            return Common.listConvertToPage(inquiryInfoNewRepository.findAllList(isDelete,inquiryCode,cargoName,status), pageable);
        }else {
            //根据不同的创建人查询询价
            return Common.listConvertToPage(inquiryInfoNewRepository.findAllListWithCreator(isDelete, inquiryCode,cargoName,status,actor.getUsername()), pageable);
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
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("inquiryId"));
        }
    }

    //询价修改
    public InquiryInfoNew update(InquiryInfoNewDto inquiryInfoNewDto) throws GlobalServiceException{
        InquiryInfoNew inquiryInfoNew = new InquiryInfoNew();
        BeanUtils.copyProperties(inquiryInfoNewDto, inquiryInfoNew);
        if(null!=inquiryInfoNewDto.getInquiryId() && 0!=inquiryInfoNewDto.getInquiryId()){
            inquiryInfoNew=inquiryInfoNewRepository.findAllByInquiryId(inquiryInfoNewDto.getInquiryId());
            //项目预算
            inquiryInfoNew.setProjectBudget(inquiryInfoNewDto.getProjectBudget()==null?inquiryInfoNew.getProjectBudget():inquiryInfoNewDto.getProjectBudget());
            //项目背景
            inquiryInfoNew.setProjectBackground(inquiryInfoNewDto.getProjectBackground()==null?inquiryInfoNew.getProjectBackground():inquiryInfoNewDto.getProjectBackground());
            //采购人
            inquiryInfoNew.setPurchaser(inquiryInfoNewDto.getPurchaser()==null?inquiryInfoNew.getPurchaser():inquiryInfoNewDto.getPurchaser());
            //厂家
            inquiryInfoNew.setManufactor(inquiryInfoNewDto.getManufactor()==null?inquiryInfoNew.getManufactor():inquiryInfoNewDto.getManufactor());
            //预算编码
            inquiryInfoNew.setBudget_coding(inquiryInfoNewDto.getBudget_coding()==null?inquiryInfoNew.getBudget_coding():inquiryInfoNewDto.getBudget_coding());
            //经办人
            inquiryInfoNew.setOperator(inquiryInfoNewDto.getOperator()==null?inquiryInfoNew.getOperator():inquiryInfoNewDto.getOperator());
            //经办人联系电话
            inquiryInfoNew.setOperator_number(inquiryInfoNewDto.getOperator_number()==null?inquiryInfoNew.getOperator_number():inquiryInfoNewDto.getOperator_number());
            //审核状态
            inquiryInfoNew.setStatus(inquiryInfoNewDto.getStatus()==null?inquiryInfoNew.getStatus():inquiryInfoNewDto.getStatus());
            inquiryInfoNew.setProjectId(inquiryInfoNewDto.getProjectId()==null?inquiryInfoNew.getProjectId():inquiryInfoNewDto.getProjectId());
        }
        //询价修改-附件
        Attachment attachment = inquiryInfoNewDto.getAttachment();
        if (attachment != null && attachment.getAttachId()!=null && 0!=attachment.getAttachId()) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                inquiryInfoNew.setAttachment(optional.get());
            }else {
                throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("修改询价带入的附件id不存在"));
            }
        }
        //询价修改-产品
        if (inquiryInfoNewDto.getCargoInfo().getCargoId()!=null && 0!=inquiryInfoNewDto.getCargoInfo().getCargoId()) {
            Optional<CargoInfo> optional = cargoInfoRepository.findById(inquiryInfoNewDto.getCargoInfo().getCargoId());
            if (optional.isPresent()) {
                inquiryInfoNew.setCargoInfo(optional.get());
            }else {
                throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("修改询价带入的产品id不存在"));
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
            String[] header = {"询价单号", "采购人", "制造商名称", "产品名称", "项目预算"};
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

                CargoInfo cargoInfo=inquiryInfoNew.getCargoInfo();//产品
                if(cargoInfo!=null){
                    cargoInfo = cargoInfoRepository.getOne(inquiryInfoNew.getCargoInfo().getCargoId());
                    row.createCell(3).setCellValue(new HSSFRichTextString(cargoInfo.getCargoName()));//产品名称
                }else {
                    row.createCell(3).setCellValue(new HSSFRichTextString(""));
                }
                row.createCell(0).setCellValue(new HSSFRichTextString(inquiryInfoNew.getInquiryCode()));
                row.createCell(1).setCellValue(new HSSFRichTextString(inquiryInfoNew.getPurchaser()));
                row.createCell(2).setCellValue(new HSSFRichTextString(inquiryInfoNew.getManufactor()));
                row.createCell(4).setCellValue(new HSSFRichTextString(inquiryInfoNew.getProjectBudget()+""));
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;fileName=inquiryInfo.xls");
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("询价导出出现异常",e);
        }
    }

    public Set<BusiJbpmFlow> getSetBusiJbpmFlowList(Long businessId,String businessType){
        Set<BusiJbpmFlow> busiJbpmFlows=busiJbpmFlowRepository.getSetBusiJbpmFlowList(businessId,businessType);
        return busiJbpmFlows;
    }
    public List<Leaders> getGroupList() {
        return leadersRepository.getGroupList();
    }

}
