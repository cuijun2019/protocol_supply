package com.etone.protocolsupply.service.template;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.template.ContractTemplateCollectionDto;
import com.etone.protocolsupply.model.dto.template.ContractTemplateDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.ContractTemplate;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.ContractTemplateRepository;
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
public class ContractTemplateService {

    @Autowired
    private ContractTemplateRepository contractTemplateRepository;
    @Autowired
    private AttachmentRepository       attachmentRepository;
    @Autowired
    private PagingMapper               pagingMapper;

    public ContractTemplate save(ContractTemplateDto contractTemplateDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();

        Attachment attachment = contractTemplateDto.getAttachment();

        ContractTemplate contractTemplate = new ContractTemplate();
        BeanUtils.copyProperties(contractTemplateDto, contractTemplate);
        contractTemplate.setCreator(userName);
        contractTemplate.setCreateDate(date);
        contractTemplate.setMaintenanceMan(userName);
        contractTemplate.setMaintenanceDate(date);
        contractTemplate.setIsDelete(Constant.DELETE_NO);
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                contractTemplate.setAttachment(optional.get());
            }
        }
        return contractTemplateRepository.save(contractTemplate);
    }

    public Specification<ContractTemplate> getWhereClause(String subject, String status, String isDelete) {
        return (Specification<ContractTemplate>) (root, criteriaQuery, criteriaBuilder) -> {

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

    public Page<ContractTemplate> findContractTemplates(Specification<ContractTemplate> specification, Pageable pageable) {
        return contractTemplateRepository.findAll(specification, pageable);
    }

    public ContractTemplateCollectionDto to(Page<ContractTemplate> source, HttpServletRequest request) {
        ContractTemplateCollectionDto contractTemplateCollectionDto = new ContractTemplateCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, contractTemplateCollectionDto, request);
        ContractTemplateDto contractTemplateDto;
        for (ContractTemplate contractTemplate : source) {
            contractTemplateDto = new ContractTemplateDto();
            BeanUtils.copyProperties(contractTemplate, contractTemplateDto);
            contractTemplateCollectionDto.add(contractTemplateDto);
        }
        return contractTemplateCollectionDto;
    }

    public ContractTemplate findOne(Long contractTemplateId) {
        Optional<ContractTemplate> optional = contractTemplateRepository.findById(contractTemplateId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过采购结果通知书模板ID"));
        }
    }

    public ContractTemplate update(ContractTemplateDto contractTemplateDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();

        ContractTemplate contractTemplate = this.findOne(contractTemplateDto.getContractTemplateId());
        Attachment attachment = contractTemplateDto.getAttachment();
        contractTemplate.setMaintenanceMan(userName);
        contractTemplate.setMaintenanceDate(date);
        if (contractTemplate != null && attachment == null) {
            SpringUtil.copyPropertiesIgnoreNull(contractTemplateDto, contractTemplate);
            contractTemplateRepository.save(contractTemplate);
        }
        if (attachment != null) {
            Optional<Attachment> optional = attachmentRepository.findById(attachment.getAttachId());
            if (optional.isPresent()) {
                contractTemplate.setAttachment(optional.get());
            }
            contractTemplateRepository.save(contractTemplate);
        }
        return contractTemplate;
    }

    public void delete(Long contractTemplateId) {
        contractTemplateRepository.updateIsDelete(contractTemplateId);
    }
}
