package com.etone.protocolsupply.repository.project;

import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProjectInfoRepository extends JpaRepository<ProjectInfo, Long>, JpaSpecificationExecutor<ProjectInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update project_info set is_delete=1 where project_id in ?1 and status=1", nativeQuery = true)
    void updateIsDelete(List<Long> projectIds);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select * from project_info where is_delete=2 and project_id=?1", nativeQuery = true)
    ProjectInfo findAllByProjectId(Long ProjectId);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select max(p.project_id)from project_info p where p.is_delete=2 limit 1", nativeQuery = true)
    String findMaxOne();

    @Query(value = "select * from project_info where is_delete=2 and project_id in (:projectIds)", nativeQuery = true)
    List<ProjectInfo> findAllp(@Param("projectIds") List<Long> projectIds);

    @Query(value = "select * from project_info where is_delete=2 and " +
            "if((:projectSubject is not null), (project_subject like %:projectSubject%), (1=1)) and if((:status is not null), (status=:status), (1=1))", nativeQuery = true)
    List<ProjectInfo> findAllp2(@Param("projectSubject") String projectSubject, @Param("status") String status);

    @Query(value = "update project_info set cargo_id=:cargoId where project_id =:projectId", nativeQuery = true)
    @Modifying
    void setCargoId(@Param("projectId") Long projectId, @Param("cargoId") Long cargoId);

    @Query(value = "select a.agent_name from agent_info_exp a " +
            "where exists (select 1 from project_info p where p.project_id = a.project_id and p.project_id = :projectId) " +
            "and a.is_recommend_supplier = 1", nativeQuery = true)
    String getAgentName(@Param("projectId") Long projectId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update project_info set purchase_id=?1 where project_id=?2", nativeQuery = true)
    void updatePurchaseId(Long attachId, Long projectId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update project_info set contract_id=?1,encryptcontract_id=?2 where project_id=?3", nativeQuery = true)
    void updateContractId(Long attachId,Long encryptAttachId, long parseLong);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update project_info set notice_id=?1,encryptnotice_id=?2 where project_id=?3", nativeQuery = true)
    void updateNoticeId(Long attachId,Long encryptAttachId, long parseLong);

    @Query(value = "select * from project_info where is_delete=:isDelete " +
            "and if((:projectSubject is not null), (project_subject like %:projectSubject%), (1=1)) " +
            "and if((:status is not null), (status=:status), (1=1)) " +
            "and if((:projectCode is not null), (project_code=:projectCode), (1=1)) " +
            "and if((:inquiryId is not null), (inquiry_id=:inquiryId), (1=1))  order by project_id desc",  nativeQuery = true)
    List<ProjectInfo> findAll(@Param("isDelete") String isDelete,@Param("projectSubject") String projectSubject,@Param("projectCode") String projectCode
            ,@Param("status") String status,@Param("inquiryId") String inquiryId );

    @Query(value = "select * from project_info where is_delete=:isDelete " +
            "and if((:projectSubject is not null), (project_subject like %:projectSubject%), (1=1)) " +
            "and if((:actor is not null), (creator=:actor), (1=1)) " +
            "and if((:status is not null), (status=:status), (1=1)) " +
            "and if((:projectCode is not null), (project_code=:projectCode), (1=1)) " +
            "and if((:inquiryId is not null), (inquiry_id=:inquiryId), (1=1))  order by project_id desc",  nativeQuery = true)
    List<ProjectInfo> findAll(@Param("isDelete") String isDelete,@Param("projectSubject") String projectSubject,@Param("projectCode") String projectCode
            ,@Param("status") String status,@Param("inquiryId") String inquiryId ,@Param("actor") String actor);

    @Query(value = "select * from project_info WHERE exists(" +
            "select 1 from busi_jbpm_flow b where project_id = b.business_id and b.business_type='projectAudit' and read_type is null " +
            "and if((:actor is not null), (b.parent_actor=:actor or b.next_actor=:actor), (1=1)))" +
            "and  is_delete=:isDelete and  if((:projectSubject is not null), (project_subject like %:projectSubject%), (1=1)) " +
            "and if((:status is not null), (status=:status), (1=1)) and if((:projectCode is not null), (project_code=:projectCode), (1=1)) " +
            "and if((:inquiryId is not null), (inquiry_id=:inquiryId), (1=1)) order by project_id desc",  nativeQuery = true)
    List<ProjectInfo> findAlltoMyProject(@Param("isDelete") String isDelete,@Param("projectSubject") String projectSubject,@Param("projectCode") String projectCode
            ,@Param("status") String status,@Param("inquiryId") String inquiryId,@Param("actor") String actor);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update project_info set project_subject=?2,purchaser=?3,currency=?4,delivery_date=?5,delivery_date_status=?6," +
            "guarantee_date=?7,guarantee_fee=?8,payment_method=?9,price_term=?10,cargoTotal=?11,amount=?12,status=?13,inquiry_id=?14," +
            "creator=?15,project_code=?16,is_delete=?17,quantity=?18 ,amountRmb=?19,foreign_trade_company=?20,default_guarantee=?21," +
            "paid_extend_warranty=?22,packing_instruction=?23,operator=?24,operator_number=?25,projectEntrustingUnit=?26,finalUser=?27,remark=?28, " +
            "exchangerate=?29,partner_contact=?30,partner_contact_number=?31,product_contact=?32,product_contact_number=?33,project_budget=?34" +
            " where project_id=?1", nativeQuery = true)
    void update(Long projectId,String projectSubject,String purchaser,String currency,String deliveryDate,Long deliveryDateStatus,
                       String guaranteeDate,String guaranteeFee,String paymentMethod,String priceTerm,Double cargoTotal,String amount,Integer status
                        ,Long inquiryId,String creator,String projectCode,Integer isDelete,String quantity,String amountRmb,
                String foreign_trade_company,String default_guarantee,String paid_extend_warranty,String packing_instruction,
                String operator,String operator_number,String projectEntrustingUnit,String finalUser,String remark,String exchangerate,
                String partner_contact,String partner_contact_number,String product_contact,String product_contact_number,Double project_budget );

    @Query(value = "select p.* from project_info p " +
            "where exists (SELECT 1 FROM busi_jbpm_flow b WHERE p.project_id = b.business_id and b.business_type=:businessType and b.parent_actor=:parentActor) " +
            "and p.status=:status and p.is_delete=:isDelete", nativeQuery = true)
    List<ProjectInfo> findAllByBusiJbpmFlow(@Param("isDelete") String isDelete,@Param("businessType") String businessType,@Param("parentActor") String parentActor
    ,@Param("status") String status);

    @Query(value = "select * from project_info WHERE exists(select 1 from busi_jbpm_flow b " +
            "where project_id = b.business_id  and if((:actor is not null), (parent_actor=:actor or next_actor=:actor ), (1=1)) " +
            "and business_type='projectAudit') and is_delete=:isDelete",  nativeQuery = true)
    List<ProjectInfo> findAlltoExpert(@Param("isDelete") Integer isDelete ,@Param("actor") String actor  );


    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update project_info set feasibility_fileId=?1 where project_id=?2", nativeQuery = true)
    void updateFeasibility_fileId(@Param("attachId") Long attachId,@Param("id") String id);

}
