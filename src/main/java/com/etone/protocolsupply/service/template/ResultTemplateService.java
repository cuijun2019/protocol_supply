package com.etone.protocolsupply.service.template;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.template.ResultTemplateCollectionDto;
import com.etone.protocolsupply.model.dto.template.ResultTemplateDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.ResultTemplate;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.ResultTemplateRepository;
import com.etone.protocolsupply.utils.Common;
import com.etone.protocolsupply.utils.PagingMapper;
import com.etone.protocolsupply.utils.SpringUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class ResultTemplateService {

    @Autowired
    private ResultTemplateRepository resultTemplateRepository;
    @Autowired
    private AttachmentRepository     attachmentRepository;
    @Autowired
    private PagingMapper             pagingMapper;

    public ResultTemplate save(ResultTemplateDto resultTemplateDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();

        Attachment attachment = resultTemplateDto.getAttachment();
        ResultTemplate resultTemplate = new ResultTemplate();
        BeanUtils.copyProperties(resultTemplateDto, resultTemplate);
        resultTemplate.setCreator(userName);
        resultTemplate.setCreateDate(date);
        resultTemplate.setMaintenanceMan(userName);
        resultTemplate.setMaintenanceDate(date);
        resultTemplate.setIsDelete(Constant.DELETE_NO);
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                resultTemplate.setAttachment(optional.get());
            }
        }
        return resultTemplateRepository.save(resultTemplate);
    }

    public Specification<ResultTemplate> getWhereClause(String subject, String status, String isDelete) {
        return (Specification<ResultTemplate>) (root, criteriaQuery, criteriaBuilder) -> {

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

    public Page<ResultTemplate> findResultTemplates(Specification<ResultTemplate> specification, Pageable pageable) {
        return resultTemplateRepository.findAll(specification, pageable);
    }

    public ResultTemplateCollectionDto to(Page<ResultTemplate> source, HttpServletRequest request) {
        ResultTemplateCollectionDto resultTemplateCollectionDto = new ResultTemplateCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, resultTemplateCollectionDto, request);
        ResultTemplateDto resultTemplateDto;
        for (ResultTemplate resultTemplate : source) {
            resultTemplateDto = new ResultTemplateDto();
            BeanUtils.copyProperties(resultTemplate, resultTemplateDto);
            resultTemplateCollectionDto.add(resultTemplateDto);
        }
        return resultTemplateCollectionDto;
    }

    public ResultTemplate findOne(Long resultTemplateId) {
        Optional<ResultTemplate> optional = resultTemplateRepository.findById(resultTemplateId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过采购结果通知书模板ID"));
        }
    }

    public ResultTemplate update(ResultTemplateDto resultTemplateDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();

        ResultTemplate resultTemplate = this.findOne(resultTemplateDto.getResultTemplateId());
        Attachment attachment = resultTemplateDto.getAttachment();
        resultTemplate.setMaintenanceMan(userName);
        resultTemplate.setMaintenanceDate(date);
        if (resultTemplate != null && attachment == null) {
            SpringUtil.copyPropertiesIgnoreNull(resultTemplateDto, resultTemplate);
            resultTemplateRepository.save(resultTemplate);
        }
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                resultTemplate.setAttachment(optional.get());
            }
            resultTemplateRepository.save(resultTemplate);
        }
        return resultTemplate;
    }

    public void delete(Long resultTemplateId) {
        resultTemplateRepository.updateIsDelete(resultTemplateId);
    }
}
