package com.etone.protocolsupply.repository.procedure;

import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface BusiJbpmFlowRepository extends JpaRepository<BusiJbpmFlow, Long>, JpaSpecificationExecutor<BusiJbpmFlow> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update busi_jbpm_flow set is_delete=1 where id=?1", nativeQuery = true)
    void updateIsDelete(Long id);


    @Query(value = "select * from busi_jbpm_flow where 1=1 and " +
            "if((:businessType is not null), (business_type =:businessType), (1=1)) and if((:businessSubject is not null), (business_subject like %:businessSubject%), (1=1)) and " +
            "id in (:ids) and type=:type", nativeQuery = true)
    List<BusiJbpmFlow> findAll(@Param("businessType") String businessType, @Param("businessSubject") String businessSubject, @Param("ids") List<Long> ids,@Param("type") Integer type);

    @Query(value = "select * from busi_jbpm_flow where 1=1 and " +
            "if((:businessType is not null), (business_type =:agentName), (1=1)) and if((:businessSubject is not null), (business_subject like %:businessSubject%), (1=1)) ", nativeQuery = true)
    List<BusiJbpmFlow> findAll(@Param("businessType") String businessType, @Param("businessSubject") String businessSubject);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update busi_jbpm_flow set type=1 where id=?1", nativeQuery = true)
    void updateType(Long id);
}
