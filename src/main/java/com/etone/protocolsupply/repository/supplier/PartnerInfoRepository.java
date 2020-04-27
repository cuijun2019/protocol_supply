package com.etone.protocolsupply.repository.supplier;

import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PartnerInfoRepository extends JpaRepository<PartnerInfo, Long>, JpaSpecificationExecutor<PartnerInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update partner_info set is_delete=1 where partner_id=?1", nativeQuery = true)
    void updateIsDelete(Long partnerId);

    @Query(value = "select * from partner_info where partner_id in (:supplierIds)",
            nativeQuery = true)
    List<PartnerInfo> findAll(@Param("supplierIds") List<Long> supplierIds);

    @Query(value = "select p.partner_id,p.company_no  from partner_info p where p.auth_status=1  and p.company_no like %?1% and p.partner_id in(select partner_id from users u where u.id in (select ur.user_id from user_role ur where ur.role_id=2))",
            nativeQuery = true)
    List<PartnerInfo> findVerifiedSuppliersByagentName(String agentName);

    @Query(value = "select p.partner_id,p.company_no  from partner_info p where p.auth_status=1  and p.partner_id in(select partner_id from users u where u.id in (select ur.user_id from user_role ur where ur.role_id=2))",
            nativeQuery = true)
    List<PartnerInfo> findVerifiedSuppliers();

}
