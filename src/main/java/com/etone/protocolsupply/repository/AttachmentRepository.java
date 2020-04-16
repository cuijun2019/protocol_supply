package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface AttachmentRepository extends JpaRepository<Attachment, Long>, JpaSpecificationExecutor<Attachment> {


    @Query(value = "select * from attachment where attach_id=(select attach_id from result_template r where r.status=1)",
            nativeQuery = true)
    Attachment findByAttachmentId();


    @Query(value = "select * from attachment where attach_id=(select attach_id from bid_template b where b.status=1)",
            nativeQuery = true)
    Attachment findBidTemplate();
}
