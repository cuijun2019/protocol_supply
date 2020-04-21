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

public interface BusiJbpmFlowRepository extends JpaRepository<BusiJbpmFlow, Long>, JpaSpecificationExecutor<BusiJbpmFlow> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update inquiry_info set is_delete=1 where inquiry_id=?1", nativeQuery = true)
    void updateIsDelete(Long inquiryId);


    @Query(value = "select * from busi_jbpm_flow where 1=1 and " +
            "if((:businessType is not null), (business_type =:businessType), (1=1)) and if((:businessSubject is not null), (business_subject like %:businessSubject%), (1=1)) and " +
            "id in (:ids)", nativeQuery = true)
    List<BusiJbpmFlow> findAll(@Param("businessType") String businessType, @Param("businessSubject") String businessSubject, @Param("ids") List<Long> ids);

    @Query(value = "select * from busi_jbpm_flow where 1=1 and " +
            "if((:businessType is not null), (business_type =:agentName), (1=1)) and if((:businessSubject is not null), (business_subject like %:businessSubject%), (1=1)) ", nativeQuery = true)
    List<BusiJbpmFlow> findAll(@Param("businessType") String businessType, @Param("businessSubject") String businessSubject);
}
