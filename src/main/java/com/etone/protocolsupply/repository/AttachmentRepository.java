package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttachmentRepository extends JpaRepository<Attachment, Long>, JpaSpecificationExecutor<Attachment> {
}
