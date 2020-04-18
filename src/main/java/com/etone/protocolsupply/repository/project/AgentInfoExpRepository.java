package com.etone.protocolsupply.repository.project;

import com.etone.protocolsupply.model.entity.project.AgentInfoExp;
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

    @Query(value = "select * from agent_info_exp where 1=1 and if((?1 is not null), (project_id=?1), (1=1)) and is_delete=?2", nativeQuery = true)
    List<AgentInfoExp> findAll(String projectId, String isDelete);


    @Query(value = "select * from agent_info_exp where project_id=?1", nativeQuery = true)
    List<AgentInfoExp> findByProjectId(Long projectId);

    @Query(value = "select * from agent_info_exp where project_id=?1", nativeQuery = true)
    AgentInfoExp findByProjectId2(Long projectId);
}
