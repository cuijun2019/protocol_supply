package com.etone.protocolsupply.repository.template;

import com.etone.protocolsupply.model.entity.template.ResultTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ResultTemplateRepository extends JpaRepository<ResultTemplate, Long>, JpaSpecificationExecutor<ResultTemplate> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update result_template set is_delete=1 where result_template_id=?1", nativeQuery = true)
    void updateIsDelete(Long resultTemplateId);
}
