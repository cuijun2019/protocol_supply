package com.etone.protocolsupply.repository.procedure;

import com.etone.protocolsupply.model.entity.procedure.BusiJbpmFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface BusiJbpmFlowRepository extends JpaRepository<BusiJbpmFlow, Long>, JpaSpecificationExecutor<BusiJbpmFlow> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update busi_jbpm_flow set is_delete=1 where id=?1", nativeQuery = true)
    void updateIsDelete(Long id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update busi_jbpm_flow set read_type=1,business_subject=?2 where id=?1  ", nativeQuery = true)
    void updateIsReadType(Long id,String businessSubject);


    @Query(value = "select * from busi_jbpm_flow where id in (:ids) ", nativeQuery = true)
    List<BusiJbpmFlow> findAll( @Param("ids") List<Long> ids);

    @Query(value = "select * from busi_jbpm_flow where 1=1 and " +
            "if((:businessType is not null), (business_type =:agentName), (1=1)) and if((:businessSubject is not null), (business_subject like %:businessSubject%), (1=1)) ", nativeQuery = true)
    List<BusiJbpmFlow> findAll(@Param("businessType") String businessType, @Param("businessSubject") String businessSubject);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update busi_jbpm_flow set type=1 where id=?1", nativeQuery = true)
    void updateType(Long id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update busi_jbpm_flow set read_type=1 where id=?1", nativeQuery = true)
    void updateReadType(Long id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update busi_jbpm_flow set next_actor=:nextActor ,action =2 where id=:id", nativeQuery = true)
    void upNextActor(@Param("id") Long id,@Param("nextActor") String nextActor);


    @Query(value = "select * from busi_jbpm_flow where  " +
            " if((:nextActor is not null),(next_actor=:nextActor),(1=1))  and if((type is not null),(type=:type),(1=1)) " +
            " and if((:readType is not null),(read_type=:readType),(1=1)) ", nativeQuery = true)
    List<BusiJbpmFlow> findAllToExpert(@Param("type") Integer type,@Param("readType") Integer readType,@Param("nextActor") String nextActor);


    @Query(value = "select * from busi_jbpm_flow where  read_type=:readType", nativeQuery = true)
    List<BusiJbpmFlow> findAllToExpertByReadType(@Param("readType") Integer readType);

    @Query(value = "select * from busi_jbpm_flow where 1=1 " +
            " and if((:businessId is not null), (business_id =:businessId), (1=1))  " +
            " and if((:businessType is not null), (business_type =:businessType), (1=1)) " +
            " and if((:nextActor is not null), (next_actor=:nextActor), (1=1))  " +
            " and type=0", nativeQuery = true)
    List<BusiJbpmFlow> isBusiJbpmFlows(@Param("businessId") String businessId,@Param("businessType") String businessType
    ,@Param("nextActor") String nextActor);

    @Query(value = "select * from busi_jbpm_flow where 1=1 " +
            " and if((:businessId is not null), (business_id =:businessId), (1=1))  " +
            " and if((:businessType is not null), (business_type =:businessType), (1=1)) " +
            " and if((:nextActor is not null), (next_actor=:nextActor), (1=1))  " +
            " and read_type=0", nativeQuery = true)
    List<BusiJbpmFlow> findBJFListWithReadType(@Param("businessId") String businessId,@Param("businessType") String businessType
            ,@Param("nextActor") String nextActor);



    @Query(value = "select * from busi_jbpm_flow where 1=1 " +
            " and if((:businessId is not null), (business_id =:businessId), (1=1))  " +
            " and if((:businessType is not null), (business_type =:businessType), (1=1)) " +
            " and if((:parentActor is not null), (parent_actor=:parentActor or next_actor=:parentActor), (1=1)) " +
            " and if((:nextActor is not null), (next_actor=:nextActor or parent_actor=:nextActor), (1=1)) ", nativeQuery = true)
    List<BusiJbpmFlow> isCover(@Param("businessId") String businessId,@Param("businessType") String businessType
            ,@Param("parentActor") String parentActor,@Param("nextActor") String nextActor);


    @Query(value = "select * from busi_jbpm_flow where 1=1 " +
            " and if((:businessId is not null), (business_id =:businessId), (1=1))  " +
            " and if((:businessType is not null), (business_type =:businessType), (1=1)) " +
            " and if((:parentActor is not null), (parent_actor=:parentActor), (1=1)) " +
            " and if((:nextActor is not null), (next_actor=:nextActor), (1=1)) " +
            " and if((:type is not null), (type =:type), (1=1)) ", nativeQuery = true)
    List<BusiJbpmFlow> isExistBusiJbpmFlows(@Param("businessId") String businessId,@Param("businessType") String businessType
            ,@Param("parentActor") String parentActor,@Param("nextActor") String nextActor,@Param("type") Integer type);


    @Query(value = "select * from busi_jbpm_flow where 1=1 " +
            " and if((:businessId is not null), (business_id =:businessId), (1=1))  " +
            " and if((:businessType is not null), (business_type =:businessType), (1=1)) " +
            " and if((:type is not null), (type=:type ), (1=1)) ", nativeQuery = true)
    List<BusiJbpmFlow> updateNextActor(@Param("businessId") String businessId,@Param("businessType") String businessType
            ,@Param("type") Integer type);


    @Query(value = "select * from busi_jbpm_flow where 1=1 " +
            " and if((:businessType is not null), (business_type =:businessType), (1=1)) " +
            " and if((:businessSubject is not null), (business_subject like %:businessSubject%), (1=1))  " +
            " and if((:type is not null), (type=:type), (1=1))  " +
            " and if((:readType is not null), (read_type=:readType), (1=1))  " +
            " and if((:businessId is not null), (business_id=:businessId), (1=1))  " +
            " and if((:parentActor is not null), (parent_actor=:parentActor), (1=1))  " +
            " and if((:nextActor is not null), (next_actor=:nextActor), (1=1))  " +
            " and if((:action is not null), (action !=:action), (1=1)) order by flow_start_time desc " +
            " ", nativeQuery = true)
    List<BusiJbpmFlow> findAllList(@Param("businessType") String businessType,@Param("businessSubject") String businessSubject
            ,@Param("type") Integer type,@Param("readType") Integer readType,@Param("businessId") String businessId,@Param("parentActor") String parentActor
            ,@Param("nextActor") String nextActor,@Param("action") Integer action);

    @Query(value = "select * from busi_jbpm_flow where 1=1 " +
            " and if((:businessType is not null), (business_type =:businessType), (1=1)) " +
            " and if((:businessSubject is not null), (business_subject like %:businessSubject%), (1=1))  " +
            " and if((:type is not null), (type=:type), (1=1))  " +
            " and if((:readType is not null), (read_type=:readType), (1=1))  " +
            " and if((:businessId is not null), (business_id=:businessId), (1=1))  " +
            " and if((:parentActor is not null), (parent_actor=:parentActor), (1=1))  " +
            " and if((:nextActor is not null), (next_actor=:nextActor), (1=1))  " +
            " and if((:action is not null), (action !=:action), (1=1))  order by flow_start_time asc" +
            " ", nativeQuery = true)
    List<BusiJbpmFlow> findAllListAsc(@Param("businessType") String businessType,@Param("businessSubject") String businessSubject
            ,@Param("type") Integer type,@Param("readType") Integer readType,@Param("businessId") String businessId,@Param("parentActor") String parentActor
            ,@Param("nextActor") String nextActor,@Param("action") Integer action);


    @Query(value = "select * from busi_jbpm_flow where business_id=?1 and business_type=?2 and type=?3 and next_actor=?4 order by flow_start_time desc", nativeQuery = true)
    BusiJbpmFlow  findsffsxjjl( String businessId,String businessType,String type,String actor);


    @Query(value = "select * from busi_jbpm_flow where business_id=?1 and business_type=?2 order by flow_start_time asc", nativeQuery = true)
    Set<BusiJbpmFlow> getSetBusiJbpmFlowList(Long businessId,String businessType);

}
