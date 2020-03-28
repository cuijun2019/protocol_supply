package com.etone.protocolsupply.repository.cargo;

import com.etone.protocolsupply.model.entity.cargo.BrandItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface BrandItemRepository extends JpaRepository<BrandItem, String>, JpaSpecificationExecutor<BrandItem> {

    @Query(value = "select * from brand_item where brand_item_id=?1", nativeQuery = true)
    List<BrandItem> findAllBybrandItemId(String brandItemId);

    @Query(value = "select * from brand_item where  parent_item_code=?1", nativeQuery = true)
    List<BrandItem> findAllByParentItemCode(String parentItemCode);

    @Modifying
    @Query(value = "call brandItemtemp(?1)   ", nativeQuery = true)
    List<BrandItem> findAllByParentItemName(String itemName);

    @Override
    @Query(value = "select * from brand_item   ", nativeQuery = true)
    List<BrandItem> findAll();
}
