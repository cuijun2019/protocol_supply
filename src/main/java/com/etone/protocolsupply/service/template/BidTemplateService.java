package com.etone.protocolsupply.service.template;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.template.BidTemplateCollectionDto;
import com.etone.protocolsupply.model.dto.template.BidTemplateDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.template.BidTemplate;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.template.BidTemplateRepository;
import com.etone.protocolsupply.utils.PagingMapper;
import com.etone.protocolsupply.utils.SpringUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class BidTemplateService {

    @Autowired
    private BidTemplateRepository bidTemplateRepository;
    @Autowired
    private AttachmentRepository  attachmentRepository;
    @Autowired
    private PagingMapper          pagingMapper;

    public BidTemplate save(BidTemplateDto bidTemplateDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();

        Attachment attachment = bidTemplateDto.getAttachment();

        BidTemplate bidTemplate = new BidTemplate();
        BeanUtils.copyProperties(bidTemplateDto, bidTemplate);
        bidTemplate.setCreator(userName);
        bidTemplate.setCreateDate(date);
        bidTemplate.setMaintenanceMan(userName);
        bidTemplate.setMaintenanceDate(date);
        bidTemplate.setIsDelete(Constant.DELETE_NO);
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                bidTemplate.setAttachment(optional.get());
            } else {
                throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过附件ID"));
            }
        }
        return bidTemplateRepository.save(bidTemplate);
    }

    public Specification<BidTemplate> getWhereClause(String subject, String status, String isDelete) {
        return (Specification<BidTemplate>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(subject)) {
                predicates.add(criteriaBuilder.equal(root.get("subject").as(String.class), subject));
            }
            if (Strings.isNotBlank(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status").as(String.class), status));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<BidTemplate> findBidTemplates(Specification<BidTemplate> specification, Pageable pageable) {
        return bidTemplateRepository.findAll(specification, pageable);
    }

    public BidTemplateCollectionDto to(Page<BidTemplate> source, HttpServletRequest request) {
        BidTemplateCollectionDto bidTemplateCollectionDto = new BidTemplateCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, bidTemplateCollectionDto, request);
        BidTemplateDto bidTemplateDto;
        for (BidTemplate bidTemplate : source) {
            bidTemplateDto = new BidTemplateDto();
            BeanUtils.copyProperties(bidTemplate, bidTemplateDto);
            bidTemplateCollectionDto.add(bidTemplateDto);
        }
        return bidTemplateCollectionDto;
    }

    public BidTemplate findOne(Long bidTemplateId) {
        Optional<BidTemplate> optional = bidTemplateRepository.findById(bidTemplateId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过采购结果通知书模板ID"));
        }
    }

    public BidTemplate update(BidTemplateDto bidTemplateDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();

        BidTemplate bidTemplate = this.findOne(bidTemplateDto.getBidTemplateId());
        Attachment attachment = bidTemplateDto.getAttachment();
        bidTemplate.setMaintenanceMan(userName);
        bidTemplate.setMaintenanceDate(date);
        SpringUtil.copyPropertiesIgnoreNull(bidTemplateDto, bidTemplate);
        if (bidTemplate != null && attachment == null) {
            bidTemplateRepository.save(bidTemplate);
        }
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                bidTemplate.setAttachment(optional.get());
            }
            bidTemplateRepository.save(bidTemplate);
        }
        return bidTemplate;
    }

    public void delete(Long bidTemplateId) {
        bidTemplateRepository.updateIsDelete(bidTemplateId);
    }


}
