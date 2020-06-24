package com.etone.protocolsupply.service.inquiry;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewDto;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoNewRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class InquiryInfoNewService {

    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private InquiryInfoNewRepository inquiryInfoNewRepository;
    @Autowired
    private PartnerInfoRepository partnerInfoRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private PagingMapper         pagingMapper;


    //新建保存
    public InquiryInfoNew save(InquiryInfoNewDto inquiryInfoNewDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        InquiryInfoNew inquiryInfoNew = new InquiryInfoNew();
        BeanUtils.copyProperties(inquiryInfoNewDto, inquiryInfoNew);

        inquiryInfoNew.setCreator(userName);//创建人
        inquiryInfoNew.setCreateDate(date);//创建时间
        inquiryInfoNew.setIsDelete(Constant.DELETE_NO);
        //以下货物信息关联询价表有待思考
        CargoInfo cargoInfo = inquiryInfoNewDto.getCargoInfo();
        if (cargoInfo != null && cargoInfo.getCargoId()!=null) {
            Optional<CargoInfo> optional = cargoInfoRepository.findById(cargoInfo.getCargoId());
            if (optional.isPresent()) {
                inquiryInfoNew.setCargoInfo(optional.get());
                inquiryInfoNew.setInquiryTheme(cargoInfo.getCargoName()+"的询价");//询价主题暂定=货物名称+“的询价”
            }
        }else {
            inquiryInfoNew.setCargoInfo(null);
        }

        inquiryInfoNewRepository.save(inquiryInfoNew);
        inquiryInfoNew.getCargoInfo().setPartInfos(null);//如果暂时setnull就会error："Could not write JSON: Infinite recursion (StackOverflowError); nested exception is com.fasterxml.jackson.databind.JsonMappingException: Infinite recursion (StackOverflowError) (through reference chain: com.etone.protocolsupply.model.dto.ResponseValue[\"data\"]->com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew[\"cargoInfo\"]->com.etone.protocolsupply.model.entity.cargo.CargoInfo[\"partInfos\"])"
        return inquiryInfoNew;
    }

    //查询list
    public Page<InquiryInfoNew> findInquiryInfoNewList(String isDelete, String inquiryTheme,String actor,Integer status, Pageable pageable) {
        //查询全部询价
        if(null == actor || actor.equals("admin")){
            return Common.listConvertToPage(inquiryInfoNewRepository.findAllList(isDelete,inquiryTheme,status), pageable);
        }else {
            //根据不同的创建人查询询价
            return Common.listConvertToPage(inquiryInfoNewRepository.findAllListWithCreator(isDelete, inquiryTheme,status,actor), pageable);
        }
    }

    //对查询得到的list进行处理
    public InquiryInfoNewCollectionDto to(Page<InquiryInfoNew> source, HttpServletRequest request) {
        InquiryInfoNewCollectionDto inquiryInfoNewCollectionDto = new InquiryInfoNewCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, inquiryInfoNewCollectionDto, request);
        InquiryInfoNewDto inquiryInfoNewDto;
        for (InquiryInfoNew inquiryInfoNew : source) {
            inquiryInfoNewDto = new InquiryInfoNewDto();
            BeanUtils.copyProperties(inquiryInfoNew, inquiryInfoNewDto);
            inquiryInfoNewCollectionDto.add(inquiryInfoNewDto);
        }
        return inquiryInfoNewCollectionDto;
    }


}
