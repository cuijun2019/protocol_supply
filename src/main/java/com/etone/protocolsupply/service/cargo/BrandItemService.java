package com.etone.protocolsupply.service.cargo;

import com.etone.protocolsupply.model.entity.cargo.BrandItem;
import com.etone.protocolsupply.repository.cargo.BrandItemRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Service
public class BrandItemService {

    @Autowired
    private BrandItemRepository brandItemRepository;


    public List<BrandItem> getWhereClause(String parentItemCode) {
        return brandItemRepository.findAllByParentItemCode(parentItemCode);
    }

    public List<BrandItem> getWhereClauseTemp(String itemName) {
        return Strings.isNotBlank(itemName) ? brandItemRepository.findAllByParentItemName(itemName) : brandItemRepository.findAll();
    }

}
