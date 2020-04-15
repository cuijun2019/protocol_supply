package com.etone.protocolsupply.repository.notice;

import com.etone.protocolsupply.model.entity.notice.BidNotice;
import com.etone.protocolsupply.model.entity.notice.ContractNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractNoticeRepository extends JpaRepository<ContractNotice, Long>, JpaSpecificationExecutor<ContractNotice> {

    Page<ContractNotice> findAll(Specification<ContractNotice> specification, Pageable pageable);

    @Query(value = "select * from contract_notice where contract_id in (:contractNoticeIds)",
            nativeQuery = true)
    List<ContractNotice> findAll(@Param("contractNoticeIds") List<Long> contractNoticeIds);


}
