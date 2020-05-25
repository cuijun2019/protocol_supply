package com.etone.protocolsupply.repository.notice;

import com.etone.protocolsupply.model.entity.notice.ResultNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultNoticeRepository  extends JpaRepository<ResultNotice, Long>, JpaSpecificationExecutor<ResultNotice> {

    @Query(value = "select * from result_notice where result_id in (:resultNoticeIds)",
            nativeQuery = true)
    List<ResultNotice> findAll(@Param("resultNoticeIds") List<Long> resultNoticeIds);

    @Query(value = "select * from result_notice where project_id =:projectId ",
            nativeQuery = true)
    ResultNotice findInfoByProjectId(@Param("projectId") String projectId);
}
