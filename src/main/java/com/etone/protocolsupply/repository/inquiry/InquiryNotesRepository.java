package com.etone.protocolsupply.repository.inquiry;

import com.etone.protocolsupply.model.entity.inquiry.InquiryInfoNew;
import com.etone.protocolsupply.model.entity.inquiry.InquiryNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InquiryNotesRepository extends JpaRepository<InquiryNotes, Long>, JpaSpecificationExecutor<InquiryNotes> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update inquiry_notes set is_delete=1 where notes_id in ?1", nativeQuery = true)
    void updateIsDelete(List<Long> notesIds);


    @Query(value = "select * from inquiry_notes " +
            "where is_delete=?1 " +
            "and if((?2 is not null), (inquiry_id = ?2), (1=1))  " +
            "and if((?3 is not null), (status = ?3), (1=1) ) " , nativeQuery = true)
    List<InquiryNotes> findAllList(String isDelete, String inquiryId, Integer status);

}
