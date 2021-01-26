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
    @Query(value = "update part_info set is_delete=1 where part_id in ?1", nativeQuery = true)
    void updateIsDelete(List<Long> cargoIds);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "select * from part_info where is_delete=2 and cargo_id=?1 and part_id in ?2", nativeQuery = true)
    List<PartInfo> findAllBycargoId(String cargoId,List<Long> partIds);


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


    @Query(value = "SELECT max( p.part_serial )  " +
            "FROM part_info p " +
            "LEFT JOIN cargo_info c ON p.cargo_id = c.cargo_id " +
            "WHERE " +
            "p.is_delete = 2 " +
            "AND c.cargo_serial = ?1 " +
            "LIMIT 1 ", nativeQuery = true)
    String findLastPartSerial(String categorySerial);

    @Query(value = "update part_info set cargo_id=:cargoId where part_id in (:partIds)", nativeQuery = true)
    @Modifying
    void setCargoId(@Param("cargoId") Long cargoId, @Param("partIds") List<Long> partIds);

    @Query(value = "update part_info set project_id=:projectId where part_id in (:partIds)", nativeQuery = true)
    @Modifying
    void setProjectId(@Param("projectId") Long projectId, @Param("partIds") List<Long> partIds);

    @Query(value = "select * from part_info p where exists (select 1 from cargo_info c where 1=1 " +
            "and if((?5 is not null),( EXISTS ( SELECT 1 FROM busi_jbpm_flow b WHERE c.cargo_id = b.business_id " +
            "AND b.business_type = 'cargoAudit' " +
            "and if((?5 is not null ),(b.parent_actor=?5),(1=1)))),(1=1)) " +
            "and c.cargo_id=p.cargo_id " +
            "and if((?3 is not null), (c.cargo_name like %?3%), (1=1)) " +
            "and if((?4 is not null), (c.cargo_name = ?4), (1=1)) ) " +
            "and if((?1 is not null), (p.cargo_id=?1), (1=1)) " +
            "and p.is_delete=?2 order by p.part_code desc", nativeQuery = true)
    List<PartInfo> findAll(String cargoId, String isDelete,String cargoName,String cName,String actor);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "select * from part_info where is_delete=2 and cargo_id=?1 ", nativeQuery = true)
    List<PartInfo> findAllBys(String cargoId);

    @Transactional(rollbackFor = Exception.class)
    @Query(value = "select * from part_info where is_delete=2 and part_id=?1", nativeQuery = true)
    PartInfo findOneModel(Long partId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from part_info  where cargo_id=?1", nativeQuery = true)
    void deleteByCargoId(Long cargoId);


}
