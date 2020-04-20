package com.etone.protocolsupply.repository.project;

import com.etone.protocolsupply.model.entity.project.PartInfoExp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface PartInfoExpRepository extends JpaRepository<PartInfoExp, Long>, JpaSpecificationExecutor<PartInfoExp> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update part_info_exp set is_delete=1 where part_id=?1", nativeQuery = true)
    void updateIsDelete(Long partId);

    @Query(value = "update part_info_exp set project_id=:projectId where part_id in (:partIds)", nativeQuery = true)
    @Modifying
    void setProjectId(@Param("projectId") Long projectId, @Param("partIds") List<Long> partIds);

    @Query(value = "select * from part_info_exp where 1=1 and if((?1 is not null), (project_id=?1), (1=1)) and is_delete=?2", nativeQuery = true)
    List<PartInfoExp> findAll(String projectId, String isDelete);

    @Query(value = "select * from part_info_exp where 1=1 and cargo_id=?1 and is_delete=2", nativeQuery = true)
    List<PartInfoExp> findByCargoId(Long cargoId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from part_info_exp  where project_id=?1", nativeQuery = true)
    void deleteByProjectId(Long projectId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "select * from part_info_exp where is_delete=2 and part_id in ?1", nativeQuery = true)
    List<PartInfoExp> findAllBypartIds(List<Long> partIds);

    @Query(value = "select * from part_info_exp where is_delete=2 and project_id=?1", nativeQuery = true)
    List<PartInfoExp> findByProjectId(long parseLong);
}
