package com.etone.protocolsupply.service.inquiry;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.inquiry.InquiryNotesCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryNotesDto;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.inquiry.InquiryNotes;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoNewRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryNotesRepository;
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
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class InquiryNotesService {

    @Autowired
    private InquiryInfoNewRepository inquiryInfoNewRepository;

    @Autowired
    private InquiryNotesRepository inquiryNotesRepository;

    @Autowired
    private PagingMapper         pagingMapper;


    //新建保存
    public InquiryNotes save(InquiryNotesDto inquiryNotesDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        InquiryNotes inquiryNotes = new InquiryNotes();
        BeanUtils.copyProperties(inquiryNotesDto, inquiryNotes);


        inquiryNotes.setCreator(userName);//创建人
        inquiryNotes.setCreateDate(date);//创建时间
        inquiryNotes.setIsDelete(Constant.DELETE_NO);
        //关联询价
        InquiryInfoNew inquiryInfoNew = inquiryNotesDto.getInquiryInfoNew();
        if (inquiryInfoNew != null && inquiryInfoNew.getInquiryId()!=null) {
            Optional<InquiryInfoNew> optional = inquiryInfoNewRepository.findById(inquiryInfoNew.getInquiryId());
            if (optional.isPresent()) {
                inquiryNotes.setInquiryInfoNew(optional.get());
            }
        }else {
            inquiryNotes.setInquiryInfoNew(null);
        }

        inquiryNotesRepository.save(inquiryNotes);
        inquiryNotes.getInquiryInfoNew().getCargoInfo().setPartInfos(null);
        //inquiryInfoNew.getCargoInfo().setPartInfos(null);//如果暂时setnull就会error："Could not write JSON: Infinite recursion (StackOverflowError); nested exception is com.fasterxml.jackson.databind.JsonMappingException: Infinite recursion (StackOverflowError) (through reference chain: com.etone.protocolsupply.model.dto.ResponseValue[\"data\"]->com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew[\"cargoInfo\"]->com.etone.protocolsupply.model.entity.cargo.CargoInfo[\"partInfos\"])"
        return inquiryNotes;
    }

    //查询list
    public Page<InquiryNotes> findInquiryNotesList(String isDelete, String inquiryId,Integer status, Pageable pageable) {
        //查询全部询价
        return Common.listConvertToPage(inquiryNotesRepository.findAllList(isDelete, inquiryId, status), pageable);

    }

    //对查询得到的list进行处理
    public InquiryNotesCollectionDto to(Page<InquiryNotes> source, HttpServletRequest request) {
        InquiryNotesCollectionDto inquiryNotesCollectionDto = new InquiryNotesCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, inquiryNotesCollectionDto, request);
        InquiryNotesDto inquiryNotesDto;
        for (InquiryNotes inquiryNotes : source) {
            inquiryNotesDto = new InquiryNotesDto();
            //必须要setnull，不然会error，Could not write JSON
            inquiryNotes.getInquiryInfoNew().getCargoInfo().setPartInfos(null);
            BeanUtils.copyProperties(inquiryNotes, inquiryNotesDto);
            inquiryNotesCollectionDto.add(inquiryNotesDto);
        }
        return inquiryNotesCollectionDto;
    }

    //批量删除
    public void delete(List<Long> notesIds) {
        inquiryNotesRepository.updateIsDelete(notesIds);
    }

}
