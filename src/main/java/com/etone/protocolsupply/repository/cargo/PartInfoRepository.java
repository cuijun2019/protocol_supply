package com.etone.protocolsupply.repository.cargo;

import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Transactional(rollbackFor = Exception.class)
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
    @Query(value = "delete from part_info where part_id in ?1", nativeQuery = true)
    void deleteAll(List<String> list);

    @Query(value = "select MIN(cargo_id)AS cargo_id from part_info where is_delete=2 and part_name like %?2% and  manufactor like %?3% GROUP BY cargo_id", nativeQuery = true)
    List<PartInfo> findAllBycon(String isDelete,String partName,String manufactor);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "call lastPartSerial(?1)", nativeQuery = true)
    String findLastPartSerial(String categorySerial);

    @Query(value = "update part_info set cargo_id=:cargoId where part_id in (:partIds)", nativeQuery = true)
    @Modifying
    void setCargoId(@Param("cargoId") Long cargoId, @Param("partIds") List<Long> partIds);

    @Query(value = "select * from part_info where 1=1 and if((?1 is not null), (cargo_id=?1), (1=1)) and is_delete=?2", nativeQuery = true)
    List<PartInfo> findAll(String cargoId, String isDelete);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "select * from part_info where is_delete=2 and cargo_id=?1 and project_id=?2", nativeQuery = true)
    List<PartInfo> findAllBys(Long cargoId,Long projectId);
}
