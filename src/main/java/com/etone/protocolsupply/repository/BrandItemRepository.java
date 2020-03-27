package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.BrandItem;
import com.etone.protocolsupply.model.entity.PartInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BrandItemRepository extends JpaRepository<BrandItem, String>, JpaSpecificationExecutor<BrandItem> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "select * from brand_item where brand_item_id=?1", nativeQuery = true)
    List<BrandItem> findAllBybrandItemId(String brandItemId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "select * from brand_item where  parent_item_code=?1", nativeQuery = true)
    List<BrandItem> findAllByParentItemCode(String parentItemCode);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "call brandItemtemp(?1)   ", nativeQuery = true)
    List<BrandItem> findAllByParentItemName(String ItemName);


    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "select * from brand_item   ", nativeQuery = true)
    List<BrandItem> findAll();
}
