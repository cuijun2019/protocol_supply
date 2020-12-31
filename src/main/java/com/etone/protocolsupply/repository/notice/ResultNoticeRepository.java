package com.etone.protocolsupply.repository.notice;

import com.etone.protocolsupply.model.entity.notice.BidNotice;
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

    @Query(value = "select * from result_notice r  " +
            " left join project_info p on p.project_id=r.project_id where p.creator=:username " +
            " and if((:projectSubject is not null), (r.project_subject like %:projectSubject%), (1=1) " +
            " and if((:projectCode is not null), (r.project_code=:projectCode), (1=1)))",  nativeQuery = true)
    List<ResultNotice> findMyResultNotices(@Param("projectCode") String projectCode, @Param("projectSubject") String projectSubject
            , @Param("username") String username );

    @Query(value = "select * from result_notice where 1=1 " +
            "and  if((:projectSubject is not null), (project_subject like %:projectSubject%), (1=1)) " +
            " and if((:projectCode is not null), (project_code=:projectCode), (1=1)) " +
            " ",  nativeQuery = true)
    List<ResultNotice> findMyResultNoticesAll( @Param("projectCode") String projectCode, @Param("projectSubject") String projectSubject);
}
