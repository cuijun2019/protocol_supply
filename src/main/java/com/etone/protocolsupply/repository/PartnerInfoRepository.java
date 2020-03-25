package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.PartInfo;
import com.etone.protocolsupply.model.entity.PartnerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PartnerInfoRepository extends JpaRepository<PartnerInfo, Long>, JpaSpecificationExecutor<PartnerInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update partner_info set is_delete=1 where partner_id=?1", nativeQuery = true)
    void updateIsDelete(Long partnerId);


}
