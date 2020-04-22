package com.etone.protocolsupply.repository.supplier;

import com.etone.protocolsupply.model.entity.supplier.PartnerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PartnerAccountRepository extends JpaRepository<PartnerAccount, Long>, JpaSpecificationExecutor<PartnerAccount> {

    @Query(value = "select * from partner_account where partner_id=?1", nativeQuery = true)
    PartnerAccount findByPartnerId(long partnerId);
}
