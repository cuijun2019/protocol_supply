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
    @Query(value = "update agent_info set is_delete=1 where agent_id in ?1", nativeQuery = true)
    void updateIsDelete(List<Long> agentIds);

    @Query(value = "select * from agent_info where is_delete=2 and " +
            "if((:agentName is not null), (agent_name like %:agentName%), (1=1)) and if((:status is not null), (status=:status), (1=1)) and " +
            "agent_id in (:agentIds)", nativeQuery = true)
    List<AgentInfo> findAll(@Param("agentName") String agentName, @Param("status") String status, @Param("agentIds") List<Long> agentIds);


    @Query(value = "select * from agent_info where is_delete=:isDelete and " +
            "if((:agentName is not null), (agent_name like %:agentName%), (1=1)) and if((:status is not null), (status=:status), (1=1))", nativeQuery = true)
    List<AgentInfo> findAll(@Param("agentName") String agentName, @Param("status") String status, @Param("isDelete") String isDelete);

    @Query(value = "select * from agent_info where exists(select 1 from busi_jbpm_flow b where agent_id = b.business_id and b.business_type='agentAudit' " +
            " and if((:actor is not null), (b.parent_actor=:actor or b.next_actor=:actor), (1=1))) " +
            " and  is_delete=:isDelete and " +
            "if((:agentName is not null), (agent_name like %:agentName%), (1=1)) and if((:status is not null), (status=:status), (1=1))", nativeQuery = true)
    List<AgentInfo> findMyAgent(@Param("agentName") String agentName, @Param("status") String status,@Param("isDelete") String isDelete,@Param("actor") String actor);

    @Query(value = "update agent_info set project_id=:projectId where agent_id in (:agentIds)", nativeQuery = true)
    @Modifying
    void setProjectId(@Param("projectId") Long projectId, @Param("agentIds") List<Long> agentIds);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update agent_info set status=?1,review_status=?2 where agent_id=?3", nativeQuery = true)
    int updateStatus(Integer status, Integer reviewStatus, Long agentId);

    @Query(value = "select * from agent_info where exists(select 1 from busi_jbpm_flow b where agent_id = b.business_id and b.business_type='agentAudit' " +
            " and if((:actor is not null), (b.parent_actor=:actor or b.next_actor=:actor), (1=1))) " +
            " and  is_delete=:isDelete ", nativeQuery = true)
    List<AgentInfo> findExpert(@Param("isDelete") Integer isDelete,@Param("actor") String actor);
}
