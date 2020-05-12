package com.etone.protocolsupply.repository.supplier;

import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PartnerInfoRepository extends JpaRepository<PartnerInfo, Long>, JpaSpecificationExecutor<PartnerInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update partner_info set is_delete=1 where partner_id=?1", nativeQuery = true)
    void updateIsDelete(Long partnerId);

    @Query(value = "select * from partner_info where partner_id in (:supplierIds)",
            nativeQuery = true)
    List<PartnerInfo> findAll(@Param("supplierIds") List<Long> supplierIds);

    @Query(value = "select p.*,u.username from  partner_info p \n" +
            "inner join users u on p.partner_id=u.partner_id\n" +
            "inner join user_role ur on u.id=ur.user_id\n" +
            "and p.auth_status=1 and ur.role_id=2 and p.company_no like %?1%",
            nativeQuery = true)
    List<Map<String,Object>> findVerifiedSuppliersByagentName(String agentName);

    @Query(value = "select p.*,u.username from  partner_info p \n" +
            "inner join users u on p.partner_id=u.partner_id\n" +
            "inner join user_role ur on u.id=ur.user_id\n" +
            "and p.auth_status=1 and ur.role_id=2",
            nativeQuery = true)
    List<Map<String,Object>> findVerifiedSuppliers();

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update partner_info set auth_date=?1,auth_method='普通认证',auth_status=1 where auth_date is null and auth_method is null and auth_status is null and is_delete=2 and register_time<?1 ", nativeQuery = true)
    void updateByRegisterTime(Date date);
}
