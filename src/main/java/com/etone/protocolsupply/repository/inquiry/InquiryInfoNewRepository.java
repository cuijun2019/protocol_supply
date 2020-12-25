package com.etone.protocolsupply.repository.inquiry;

import com.etone.protocolsupply.model.dto.inquiry.InquiryInfoNewDto;
import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InquiryInfoNewRepository extends JpaRepository<InquiryInfoNew, Long>, JpaSpecificationExecutor<InquiryInfoNew> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update inquiry_info_new set is_delete=1 where inquiry_id in ?1", nativeQuery = true)
    void updateIsDelete(List<Long> inquiryIds);


    @Query(value = "select * from inquiry_info_new " +
            "where is_delete=?1 " +
            "and if((?2 is not null), (inquiry_code like %?2%), (1=1))  " +
            "and if((?4 is not null), (status = ?4), (1=1) ) " +
            "and if((?3 is not null), (exists (select 1 from cargo_info c where c.cargo_id = cargo_id and c.cargo_name like %?3%)), (1=1))" +
            "order by create_date desc", nativeQuery = true)
    List<InquiryInfoNew> findAllList(String isDelete,String inquiryCode,String cargoName,Integer status);

    @Query(value = "select * from inquiry_info_new  " +
            "where exists(select 1 from busi_jbpm_flow b where inquiry_id = b.business_id and b.business_type='enquiryAudit' and read_type is null " +
            "and if((?5 is not null), (b.parent_actor=?5 or b.next_actor=?5), (1=1))) " +
            "and  is_delete=?1 " +
            "and if((?2 is not null), (inquiry_code like %?2%), (1=1))  " +
            "and if((?4 is not null), (status = ?4), (1=1) )" +
            "and if((?3 is not null), (exists (select 1 from cargo_info c where c.cargo_id = cargo_id and c.cargo_name like %?3%)), (1=1))" +
            "order by create_date desc ", nativeQuery = true)
    List<InquiryInfoNew> findAllListWithCreator(String isDelete,String inquiryCode,String cargoName,Integer status,String actor);

//    @Transactional(rollbackFor = Exception.class)
//    @Query(value = "select max(i.inquiry_code)from inquiry_info_new i where i.is_delete=2 and i.cargo_id=?1  limit 1", nativeQuery = true)
//    String findMaxOne(String cargoId);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select max(i.inquiry_code)from inquiry_info_new i where i.is_delete=2  limit 1", nativeQuery = true)
    String findMaxOne();

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select * from inquiry_info_new where is_delete=2 and inquiry_id=?1", nativeQuery = true)
    InquiryInfoNew findAllByInquiryId(Long inquiryId);

    @Query(value = "select * from inquiry_info_new where is_delete=2 and inquiry_id in ?1  ", nativeQuery = true)
    List<InquiryInfoNew> findByInquiryIds(List<Long> inquiryIds);

//    @Query(value = "select i.* from inquiry_info_new i where exists(select 1 from busi_jbpm_flow b where i.inquiry_id = b.business_id and b.business_type='enquiryAudit' " +
//            " and if((?2 is not null), (b.parent_actor=?2 or b.next_actor=?2), (1=1)))" +
//            " and i.is_delete=?1 ", nativeQuery = true)
@Query(value = "select i.* from inquiry_info_new i where i.purchaser = ?2  " +
        " and i.is_delete=?1 ", nativeQuery = true)
    List<InquiryInfoNew> findExpert(Integer isDelete,String actor);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select * from inquiry_info_new where inquiry_id=(select p.inquiry_id from project_info p where p.inquiry_id =inquiry_id and p.project_id=?1 ) and is_delete=2  ", nativeQuery = true)
    InquiryInfoNew findAllByProjectId(Long ProjectId);

    @Query(value = "select a.*  from (" +
            " select i.* " +
            " FROM inquiry_info_new i left join cargo_info c on c.cargo_id=i.cargo_id " +
            " where i.funds_card_number=?1 and c.item_name=?2 )a " +
            " GROUP BY a.inquiry_id,a.inquiry_code,a.funds_card_number,a.project_budget,a.project_background order by a.create_date desc", nativeQuery = true)
    List<InquiryInfoNew> getProjectInfoBudget(String  inquiryCode, String itemName);
}
