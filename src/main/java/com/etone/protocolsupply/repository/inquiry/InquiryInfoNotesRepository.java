package com.etone.protocolsupply.repository.inquiry;

import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InquiryInfoNotesRepository extends JpaRepository<InquiryInfoNotes, Long>, JpaSpecificationExecutor<InquiryInfoNotes> {



    @Query(value = "select * from inquiry_notes " +
            "where is_delete=?1 " +
            "and if((?2 is not null), (status = ?2), (1=1) ) " , nativeQuery = true)
    List<InquiryInfoNotes> findAllList(String isDelete, Integer status);

    @Query(value = "select * from inquiry_notes " +
            "where is_delete=?1 " +
            "and if((?2 is not null), (status = ?2), (1=1) " +
            "and if((?3 is not null), (creator = ?3),(1=1)) )" , nativeQuery = true)
    List<InquiryInfoNotes> findAllListWithCreator(String isDelete,  Integer status, String actor);

    @Query(value = "select * from inquiry_notes " +
            "where is_delete=?1 " +
            "and if((?2 is not null), (inquiry_id = ?2), (1=1) ) order by create_date desc limit 1 " , nativeQuery = true)
    InquiryInfoNotes findNotesByInquiryId(Integer isDelete ,String inquiryId);

}
