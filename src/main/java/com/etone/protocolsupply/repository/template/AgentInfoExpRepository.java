package com.etone.protocolsupply.repository.template;

import com.etone.protocolsupply.model.entity.AgentInfoExp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AgentInfoExpRepository extends JpaRepository<AgentInfoExp, Long>, JpaSpecificationExecutor<AgentInfoExp> {

    @Query(value = "update agent_info_exp set project_id=:projectId where agent_id in (:agentIds)", nativeQuery = true)
    @Modifying
    void setProjectId(@Param("projectId") Long projectId, @Param("agentIds") List<Long> agentIds);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from agent_info_exp  where project_id=?1", nativeQuery = true)
    void deleteByProjectId(Long projectId);
}
