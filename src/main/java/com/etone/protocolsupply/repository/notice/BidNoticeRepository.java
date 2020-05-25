package com.etone.protocolsupply.repository.notice;

import com.etone.protocolsupply.model.entity.notice.BidNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface BidNoticeRepository extends JpaRepository<BidNotice, Long>, JpaSpecificationExecutor<BidNotice> {

    @Override
    Page<BidNotice> findAll(Specification<BidNotice> specification, Pageable pageable);

    @Query(value = "select * from bid_notice where 1=1" +
            "if((:projectCode is not null), (and project_code like %:projectCode%), (and 1=1)) " +
            "if((:projectSubject is not null), (and project_subject like %:projectSubject%), (and 1=1)) " +
            "and bid_id in (:bidNoticeIds)",
            nativeQuery = true)
    List<BidNotice> findAll(@Param("projectCode") String projectCode, @Param("projectSubject") String projectSubject, @Param("bidNoticeIds") List<Long> bidNoticeIds);

    @Query(value = "select * from bid_notice where 1=1 " +
            "and if((:projectCode is not null), (project_code like %:projectCode%), (1=1)) " +
            "and if((:projectSubject is not null), (project_subject like %:projectSubject%), (1=1)) ",
            nativeQuery = true)
    List<BidNotice> findAll(@Param("projectCode") String projectCode, @Param("projectSubject") String projectSubject);

    @Query(value = "select * from bid_notice where bid_id in (:bidNoticeIds)",
            nativeQuery = true)
    List<BidNotice> findAll(@Param("bidNoticeIds") List<Long> bidNoticeIds);

}
