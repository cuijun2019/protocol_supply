package com.etone.protocolsupply.repository.template;

import com.etone.protocolsupply.model.entity.AgentInfoExp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgentInfoExpRepository extends JpaRepository<AgentInfoExp, Long>, JpaSpecificationExecutor<AgentInfoExp> {

    @Query(value = "update agent_info_exp set project_id=:projectId where agent_id in (:agentIds)", nativeQuery = true)
    @Modifying
    void setProjectId(@Param("projectId") Long projectId, @Param("agentIds") List<Long> agentIds);
}
