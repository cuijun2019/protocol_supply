package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.ContractTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ContractTemplateRepository extends JpaRepository<ContractTemplate, Long>, JpaSpecificationExecutor<ContractTemplate> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update contract_template set is_delete=1 where contract_template_id=?1", nativeQuery = true)
    void updateIsDelete(Long contractTemplateId);
}
