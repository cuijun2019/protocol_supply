package com.etone.protocolsupply.service.partner;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.partner.PartnerInfoDto;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.repository.cargo.CargoInfoRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import com.etone.protocolsupply.utils.PagingMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
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
    private PagingMapper       pagingMapper;

    public void save(PartnerInfoDto partnerInfoDto) throws GlobalServiceException {
        PartnerInfo partnerInfo = new PartnerInfo();
        BeanUtils.copyProperties(partnerInfoDto, partnerInfo);
        partnerInfo.setIsDelete(Constant.DELETE_NO);
        partnerInfoRepository.save(partnerInfo);
    }

    public Specification<PartnerInfo> getWhereClause(String isDelete) {
        return (Specification<PartnerInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }



}
