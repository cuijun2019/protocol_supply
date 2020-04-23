package com.etone.protocolsupply.repository.inquiry;

import com.etone.protocolsupply.model.entity.inquiry.InquiryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InquiryInfoRepository extends JpaRepository<InquiryInfo, Long>, JpaSpecificationExecutor<InquiryInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update inquiry_info set is_delete=1 where inquiry_id=?1", nativeQuery = true)
    void updateIsDelete(Long inquiryId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update inquiry_info set status =?2 where inquiry_id=?1", nativeQuery = true)
    void updateStatus(Long inquiryId,String status);


    @Query(value = "select i.* from inquiry_info i where i.is_delete=?1 and if((?3 is not null), (i.inquiry_code like %?3%), (1=1)) and \n" +
            "if((?2 is not null), (exists (select 1 from cargo_info c where c.cargo_id = i.cargo_id and c.cargo_name like %?2%)), (1=1))", nativeQuery = true)
    List<InquiryInfo> findAll(String isDelete, String cargoName, String inquiryCode);


    @Query(value = "select * from inquiry_info where is_delete=2 and inquiry_id in ?1  ", nativeQuery = true)
    List<InquiryInfo> findByInquiryIds(List<Long> inquiryIds);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select max(i.inquiry_id)from inquiry_info i where i.is_delete=2 limit 1", nativeQuery = true)
    String findMaxOne();

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select * from inquiry_info where is_delete=2 and inquiry_id=?1", nativeQuery = true)
    InquiryInfo findAllByInquiryId(Long inquiryId);
}
