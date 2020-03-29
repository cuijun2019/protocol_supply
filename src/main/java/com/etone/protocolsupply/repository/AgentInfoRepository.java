package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.AgentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AgentInfoRepository extends JpaRepository<AgentInfo, Long>, JpaSpecificationExecutor<AgentInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update agent_info set is_delete=1 where agent_id=?1", nativeQuery = true)
    void updateIsDelete(Long agentId);

    @Query(value = "select * from agent_info where is_delete=2 and " +
            "if((?1 is not null), (agent_name like %?1%), (1=1)) and if((?2 is not null), (status like %?2%), (1=1))", nativeQuery = true)
    List<AgentInfo> findAll(String agentName, String status);
}
