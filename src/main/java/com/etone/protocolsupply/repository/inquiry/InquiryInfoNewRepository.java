package com.etone.protocolsupply.repository.inquiry;

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
            "and if((?2 is not null), (inquiry_theme like %?2%), (1=1))  " +
            "and if((?3 is not null), (status = ?3), (1=1) )", nativeQuery = true)
    List<InquiryInfoNew> findAllList(String isDelete,String inquiryTheme,Integer status);

    @Query(value = "select * from inquiry_info_new " +
            "where is_delete=?1 " +
            "and if((?2 is not null), (inquiry_theme like %?2%), (1=1))  " +
            "and if((?3 is not null), (status = ?3), (1=1) " +
            "and if((?4 is not null), (creator = ?4),(1=1)) )", nativeQuery = true)
    List<InquiryInfoNew> findAllListWithCreator(String isDelete,String inquiryTheme,Integer status,String actor);
}
