package com.etone.protocolsupply.repository.notice;

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


    @Query(value = "select * from contract_notice c where if((?3 is not null), (c.project_code like %?3%), (1=1)) and if((?4 is not null), (c.project_subject like %?4%), (1=1)) and p.is_delete=2 order by c.contract_id desc limit ?2 offset ?1",
            nativeQuery = true)
    List<ContractNotice> findByCondition(int i, Integer pageSize, String projectCode, String projectSubject);

    @Query(value = "SELECT c.* FROM project_info p  inner join contract_notice c on p.project_code=c.project_code where p.creator=?5 and if((?3 is not null), (c.project_code like %?3%), (1=1)) and if((?4 is not null), (c.project_subject like %?4%), (1=1)) and p.is_delete=2 order by c.contract_id desc limit ?2 offset ?1",
            nativeQuery = true)
    List<ContractNotice> findBySupplierCondition(int i, Integer pageSize, String projectCode, String projectSubject, String username);

    @Query(value = "select c.* from agent_info_exp a inner join project_info p on a.project_id=p.project_id inner join contract_notice c on p.project_code=c.project_code  where agent_name=?5 and if((?3 is not null), (c.project_code like %?3%), (1=1)) and if((?4 is not null), (c.project_subject like %?4%), (1=1)) and p.is_delete=2 and a.is_delete=2 order by c.contract_id desc limit ?2 offset ?1",
            nativeQuery = true)
    List<ContractNotice> findByAgentCondition(int i, Integer pageSize, String projectCode, String projectSubject, String username);
}
