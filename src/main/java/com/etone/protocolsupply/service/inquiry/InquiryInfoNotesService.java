package com.etone.protocolsupply.service.inquiry;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNotesCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNotesDto;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNotes;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoNewRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoNotesRepository;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
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

@Transactional(rollbackFor = Exception.class)
@Service
public class InquiryInfoNotesService {


    @Autowired
    private InquiryInfoNotesRepository inquiryInfoNotesRepository;
    @Autowired
    private InquiryInfoNewRepository inquiryInfoNewRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private BusiJbpmFlowRepository busiJbpmFlowRepository;

    @Autowired
    private PagingMapper         pagingMapper;


    //新建保存
    public InquiryInfoNotes save(InquiryInfoNotesDto inquiryInfoNotesDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        InquiryInfoNotes inquiryInfoNotes = new InquiryInfoNotes();
        BeanUtils.copyProperties(inquiryInfoNotesDto, inquiryInfoNotes);

        InquiryInfoNew inquiryInfoNew=inquiryInfoNewRepository.findAllByInquiryId(inquiryInfoNotesDto.getInquiryId());
        inquiryInfoNotes.setInquiryInfoNew(inquiryInfoNew);
        inquiryInfoNotes.setStatus(1);
        inquiryInfoNotes.setCreator(userName);//创建人
        inquiryInfoNotes.setCreateDate(date);//创建时间
        inquiryInfoNotes.setIsDelete(Constant.DELETE_NO);
        inquiryInfoNotesRepository.save(inquiryInfoNotes);
        inquiryInfoNotes.getInquiryInfoNew().getCargoInfo().setPartInfos(null);
        return inquiryInfoNotes;
    }

    //查询list
    public Page<InquiryInfoNotes> findInquiryInfoNotesList(String isDelete,String actor,Integer status, Pageable pageable) {
        //查询全部询价
        if(null == actor || actor.equals("admin")){
            return Common.listConvertToPage(inquiryInfoNotesRepository.findAllList(isDelete,status), pageable);
        }else {
            //根据不同的创建人查询询价
            return Common.listConvertToPage(inquiryInfoNotesRepository.findAllListWithCreator(isDelete,status,actor), pageable);
        }
    }

    //对查询得到的list进行处理
    public InquiryInfoNotesCollectionDto to(Page<InquiryInfoNotes> source, HttpServletRequest request) {
        InquiryInfoNotesCollectionDto inquiryInfoNotesCollectionDto = new InquiryInfoNotesCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, inquiryInfoNotesCollectionDto, request);
        InquiryInfoNotesDto inquiryInfoNewDto;
        for (InquiryInfoNotes inquiryInfoNotes : source) {
            inquiryInfoNewDto = new InquiryInfoNotesDto();
            inquiryInfoNotes.getInquiryInfoNew().getCargoInfo().setPartInfos(null);
            BeanUtils.copyProperties(inquiryInfoNotes, inquiryInfoNewDto);
            inquiryInfoNotesCollectionDto.add(inquiryInfoNewDto);
        }
        return inquiryInfoNotesCollectionDto;
    }

    public InquiryInfoNotes findNotesByInquiryId(String inquiryId){
        return inquiryInfoNotesRepository.findNotesByInquiryId(Constant.DELETE_NO,inquiryId);

    }


}
