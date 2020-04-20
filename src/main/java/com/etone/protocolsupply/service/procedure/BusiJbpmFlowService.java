package com.etone.protocolsupply.service.procedure;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.agent.AgentCollectionDto;
import com.etone.protocolsupply.model.dto.agent.AgentInfoDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryCollectionDto;
import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowCollectionDto;
import com.etone.protocolsupply.model.dto.procedure.BusiJbpmFlowDto;
import com.etone.protocolsupply.model.entity.AgentInfo;
import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.inquiry.InquiryInfoRepository;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class BusiJbpmFlowService {

    @Autowired
    private CargoInfoRepository  cargoInfoRepository;
    @Autowired
    private BusiJbpmFlowRepository busiJbpmFlowRepository;
    @Autowired
    private PartnerInfoRepository partnerInfoRepository;
    @Autowired
    private PagingMapper         pagingMapper;


    public BusiJbpmFlow save(BusiJbpmFlowDto busiJbpmFlowDto, JwtUser jwtUser) throws GlobalServiceException {
        Date date = new Date();
        String userName = jwtUser.getUsername();
        BusiJbpmFlow busiJbpmFlow = new BusiJbpmFlow();
        BeanUtils.copyProperties(busiJbpmFlowDto, busiJbpmFlow);
        busiJbpmFlow.setFlowStartTime(date);//询价时间
        busiJbpmFlow.setFlowInitorId(userName);
        busiJbpmFlow = busiJbpmFlowRepository.save(busiJbpmFlow);
        return busiJbpmFlow;
    }

    public Specification<BusiJbpmFlow> getWhereClause(String businessType, String businessSubject ) {
        return (Specification<BusiJbpmFlow>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (Strings.isNotBlank(businessType)) {
                predicates.add(criteriaBuilder.equal(root.get("businessType").as(String.class), businessType));
            }
            if (Strings.isNotBlank(businessSubject)) {
                predicates.add(criteriaBuilder.like(root.get("businessSubject").as(String.class), '%'+businessSubject+'%'));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<BusiJbpmFlow> findAgents(Specification<BusiJbpmFlow> specification, Pageable pageable) {
        return busiJbpmFlowRepository.findAll(specification, pageable);
    }

    public BusiJbpmFlowCollectionDto to(Page<BusiJbpmFlow> source, HttpServletRequest request,JwtUser jwtUser) {
        BusiJbpmFlowCollectionDto busiJbpmFlowCollectionDto = new BusiJbpmFlowCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, busiJbpmFlowCollectionDto, request);
        BusiJbpmFlowDto busiJbpmFlowDto;
        for (BusiJbpmFlow busiJbpmFlow : source) {
            busiJbpmFlowDto = new BusiJbpmFlowDto();
            BeanUtils.copyProperties(busiJbpmFlow, busiJbpmFlowDto);
            busiJbpmFlowDto.setParentActor(jwtUser.getUsername());
            busiJbpmFlowCollectionDto.add(busiJbpmFlowDto);
        }
        return busiJbpmFlowCollectionDto;
    }

}
