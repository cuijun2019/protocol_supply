package com.etone.protocolsupply.repository.procedure;

import com.etone.protocolsupply.model.entity.procedure.BusiApproveResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusiApproveResultRepository extends JpaRepository<BusiApproveResult, Long>, JpaSpecificationExecutor<BusiApproveResult> {


    @Query(value = "select * from busi_approve_result where 1=1 and approve_id in (:ids)", nativeQuery = true)
    List<BusiApproveResult> findAll( @Param("ids") List<Long> ids);

}
