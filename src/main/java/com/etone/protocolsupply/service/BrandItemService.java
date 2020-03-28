package com.etone.protocolsupply.service;

import com.etone.protocolsupply.model.entity.BrandItem;
import com.etone.protocolsupply.repository.BrandItemRepository;
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
        if(itemName!=null &&!itemName.equals("")){
            return brandItemRepository.findAllByParentItemName(itemName);
        }else {
            return brandItemRepository.findAll();
        }
    }

}
