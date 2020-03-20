package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.AgentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AgentInfoRepository extends JpaRepository<AgentInfo, Long>, JpaSpecificationExecutor<AgentInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update agent_info set is_delete=1 where agent_id=?1", nativeQuery = true)
    void updateIsDelete(Long agentId);
}
