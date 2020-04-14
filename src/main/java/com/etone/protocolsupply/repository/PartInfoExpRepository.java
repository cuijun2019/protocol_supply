package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.cargo.PartInfoExp;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
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



    @Query(value = "update part_info_exp set project_id=:projectId,cargo_id=:cargoId where part_id in (:partIds)", nativeQuery = true)
    @Modifying
    void setProjectId(@Param("projectId") Long projectId,@Param("cargoId") Long cargoId, @Param("partIds") List<Long> partIds);

    @Query(value = "select * from part_info_exp where 1=1 and if((?1 is not null), (cargo_id=?1), (1=1)) and is_delete=?2", nativeQuery = true)
    List<PartInfo> findAll(String cargoId, String isDelete);

}
