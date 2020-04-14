package com.etone.protocolsupply.repository.project;

import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import com.etone.protocolsupply.model.entity.project.ProjectInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProjectInfoRepository extends JpaRepository<ProjectInfo, Long>, JpaSpecificationExecutor<ProjectInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update project_info set is_delete=1 where project_id=?1", nativeQuery = true)
    void updateIsDelete(Long projectId);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select * from project_info where is_delete=2 and project_id=?1", nativeQuery = true)
    ProjectInfo findAllByProjectId(Long ProjectId);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select max(p.project_id)from project_info p where p.is_delete=2 limit 1", nativeQuery = true)
    String findMaxOne();

    @Query(value = "select * from project_info where is_delete=2 and " +
            "if((:projectSubject is not null), (project_subject like %:projectSubject%), (1=1)) and if((:status is not null), (status=:status), (1=1)) and " +
            "project_id in (:projectIds)", nativeQuery = true)
    List<ProjectInfo> findAllp(@Param("projectSubject") String projectSubject, @Param("status") String status, @Param("projectIds") List<Long> projectIds);

    @Query(value = "select * from project_info where is_delete=2 and " +
            "if((:projectSubject is not null), (project_subject like %:projectSubject%), (1=1)) and if((:status is not null), (status=:status), (1=1))", nativeQuery = true)
    List<ProjectInfo> findAllp2(@Param("projectSubject") String projectSubject, @Param("status") String status);

    @Query(value = "update project_info set cargo_id=:cargoId where project_id =:projectId", nativeQuery = true)
    @Modifying
    void setCargoId(@Param("projectId") Long projectId,@Param("cargoId") Long cargoId);
}
