package com.etone.protocolsupply.repository.template;

import com.etone.protocolsupply.model.entity.template.BidTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BidTemplateRepository extends JpaRepository<BidTemplate, Long>, JpaSpecificationExecutor<BidTemplate> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update bid_template set is_delete=1 where bid_template_id=?1", nativeQuery = true)
    void updateIsDelete(Long bidTemplateId);
}
