package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.PartInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PartInfoRepository extends JpaRepository<PartInfo, Long>, JpaSpecificationExecutor<PartInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update part_info set is_delete=1 where part_id=?1", nativeQuery = true)
    void updateIsDelete(Long partId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "select * from part_info where is_delete=2 and cargo_id=?1", nativeQuery = true)
    List<PartInfo> findAllBycargoId(Long cargoId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from part_info  where part_id=?1", nativeQuery = true)
    void deleteByPartId(Long partId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from part_info  where part_id in ?1", nativeQuery = true)
    void deleteAll(List<String> list);


    @Query(value = "select  MIN(cargo_id)AS cargo_id from part_info where is_delete=2 and part_name like '%?2%' and  manufactor like '%?3%' GROUP BY cargo_id", nativeQuery = true)
    List<PartInfo> findAllBycon(String isDelete,String partName,String manufactor);

}
