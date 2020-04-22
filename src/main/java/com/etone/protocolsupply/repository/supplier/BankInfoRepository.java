package com.etone.protocolsupply.repository.supplier;

import com.etone.protocolsupply.model.entity.supplier.BankInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BankInfoRepository extends JpaRepository<BankInfo,Long>, JpaSpecificationExecutor<BankInfo> {

    @Query(value = "select * from bank_info where partner_id=?1", nativeQuery = true)
    BankInfo findByPartnerId(long partnerId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update bank_info set attach_id=?1 where bank_id=?2", nativeQuery = true)
    void updateAttachId(Long attachId, Long bankId);
}
