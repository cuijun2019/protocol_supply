package com.etone.protocolsupply.service.partner;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.ExcelHeaderColumnPojo;
import com.etone.protocolsupply.model.dto.part.PartCollectionDto;
import com.etone.protocolsupply.model.dto.part.PartInfoDto;
import com.etone.protocolsupply.model.dto.partner.PartnerInfoDto;
import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.CargoInfo;
import com.etone.protocolsupply.model.entity.PartInfo;
import com.etone.protocolsupply.model.entity.PartnerInfo;
import com.etone.protocolsupply.repository.CargoInfoRepository;
import com.etone.protocolsupply.repository.PartInfoRepository;
import com.etone.protocolsupply.repository.PartnerInfoRepository;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.json.JSONObject;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
