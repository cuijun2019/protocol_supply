package com.etone.protocolsupply.repository.user;

import com.etone.protocolsupply.model.entity.user.Leaders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LeadersRepository extends JpaRepository<Leaders, Long>, JpaSpecificationExecutor<Leaders> {

    @Query(value = "SELECT * FROM leaders " , nativeQuery = true)
    List<Leaders> getGroupList();

}
