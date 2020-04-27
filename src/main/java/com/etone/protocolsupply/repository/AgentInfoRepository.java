package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.AgentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AgentInfoRepository extends JpaRepository<AgentInfo, Long>, JpaSpecificationExecutor<AgentInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update agent_info set is_delete=1 where agent_id=?1", nativeQuery = true)
    void updateIsDelete(Long agentId);

    @Query(value = "select * from agent_info where is_delete=2 and " +
            "if((:agentName is not null), (agent_name like %:agentName%), (1=1)) and if((:status is not null), (status=:status), (1=1)) and " +
            "agent_id in (:agentIds)", nativeQuery = true)
    List<AgentInfo> findAll(@Param("agentName") String agentName, @Param("status") String status, @Param("agentIds") List<Long> agentIds);

    @Query(value = "select * from agent_info where is_delete=2 and " +
            "if((:agentName is not null), (agent_name like %:agentName%), (1=1)) and if((:status is not null), (status=:status), (1=1))", nativeQuery = true)
    List<AgentInfo> findAll(@Param("agentName") String agentName, @Param("status") String status);

    @Query(value = "update agent_info set project_id=:projectId where agent_id in (:agentIds)", nativeQuery = true)
    @Modifying
    void setProjectId(@Param("projectId") Long projectId, @Param("agentIds") List<Long> agentIds);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update agent_info set status=?1 where agent_id=?2", nativeQuery = true)
    void updateStatus(Integer status, Long agentId);
}
