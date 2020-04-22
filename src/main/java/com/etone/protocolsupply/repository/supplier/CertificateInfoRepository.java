package com.etone.protocolsupply.repository.supplier;

import com.etone.protocolsupply.model.entity.supplier.CertificateInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CertificateInfoRepository extends JpaRepository<CertificateInfo, Long>, JpaSpecificationExecutor<CertificateInfo> {

    @Query(value = "select * from certificate_info where partner_id=?1", nativeQuery = true)
    CertificateInfo findByPartnerId(long partnerId);


    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update certificate_info set bank_attach=?1,license=?2 where certificate_id=?3", nativeQuery = true)
    void updateAttachmentIds(Long bankAttachId, Long licenseAttachId, Long certificateId);
}
