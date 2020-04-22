package com.etone.protocolsupply.repository.supplier;

import com.etone.protocolsupply.model.entity.supplier.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long>, JpaSpecificationExecutor<ContactInfo> {

    @Query(value = "select * from contact_info where partner_id=?1", nativeQuery = true)
    List<ContactInfo> findByPartnerId(long partnerId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update contact_info set partner_id=?1 where contact_id=?2", nativeQuery = true)
    void updatePartnerId(Long partnerId, Long contactId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from contact_info where partner_id=?1", nativeQuery = true)
    void deleteByPartnerId(Long partnerId);

}
