package com.etone.protocolsupply.repository.cargo;

import com.etone.protocolsupply.model.entity.cargo.CargoInfo;
import com.etone.protocolsupply.model.entity.cargo.PartInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CargoInfoRepository extends JpaRepository<CargoInfo, Long>, JpaSpecificationExecutor<CargoInfo> {

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update cargo_info set is_delete=1 where cargo_id=?1", nativeQuery = true)
    void updateIsDelete(Long cargoId);

    @Query(value = "select * from cargo_info where is_delete=2 and cargo_id=?1", nativeQuery = true)
    CargoInfo findAllByCargoId(Long cargoId);

    @Query(value = "select * from cargo_info where is_delete=2 and cargo_name like '%?1%'  ", nativeQuery = true)
    List<CargoInfo> findByCargoName(String cargoName);


    @Query(value = "SELECT * FROM cargo_info where 1=1 and is_delete= ?1 and cargo_name like '%?2%' and cargo_id in ?3", nativeQuery = true)
    Specification<CargoInfo> findAllBycon(String isDelete, String cargoName, List<PartInfo> list);

    @Query(value = "select cargo_serial from cargo_info where is_delete=2 order by create_date desc limit 1", nativeQuery = true)
    String findLastCargoSerial();
}
