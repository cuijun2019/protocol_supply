package com.etone.protocolsupply.service;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.entity.PartInfo;
import com.etone.protocolsupply.repository.PartInfoRepository;
import com.etone.protocolsupply.utils.PagingMapper;
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
import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Service
public class PartInfoService {

    @Autowired
    private PartInfoRepository partInfoRepository;
    @Autowired
    private PagingMapper       pagingMapper;

    public void save(PartInfoDto partInfoDto) throws GlobalServiceException {
        PartInfo partInfo = new PartInfo();
        BeanUtils.copyProperties(partInfoDto, partInfo);
        partInfo.setIsDelete(Constant.DELETE_NO);
        partInfoRepository.save(partInfo);
    }

    public Specification<PartInfo> getWhereClause(String isDelete) {
        return (Specification<PartInfo>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<PartInfo> findPartInfos(Specification<PartInfo> specification, Pageable pageable) {
        return partInfoRepository.findAll(specification, pageable);
    }

    public PartCollectionDto to(Page<PartInfo> source, HttpServletRequest request) {
        PartCollectionDto partCollectionDto = new PartCollectionDto();
        pagingMapper.storeMappedInstanceBefore(source, partCollectionDto, request);
        PartInfoDto partInfoDto;
        for (PartInfo partInfo : source) {
            partInfoDto = new PartInfoDto();
            BeanUtils.copyProperties(partInfo, partInfoDto);
            partCollectionDto.add(partInfoDto);
        }
        return partCollectionDto;
    }
}
