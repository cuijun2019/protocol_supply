package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface AttachmentRepository extends JpaRepository<Attachment, Long>, JpaSpecificationExecutor<Attachment> {


    @Query(value = "select * from attachment where attach_id=(select attach_id from result_template r where r.status=1 and r.is_delete=2)",
            nativeQuery = true)
    Attachment findByResultTemplate();


    @Query(value = "select * from attachment where attach_id=(select attach_id from bid_template b where b.status=1 and b.is_delete=2)",
            nativeQuery = true)
    Attachment findBidTemplate();

    @Query(value = "select * from attachment where attach_id=(select attach_id from contract_template c where c.status=1 and c.is_delete=2)",
            nativeQuery = true)
    Attachment findContractTemplate();

    @Query(value = "SELECT * FROM attachment where attach_name=?1 order by upload_time desc limit 1 ",
            nativeQuery = true)
    Attachment findAttachmentByName(String attachName);

}
