package com.etone.protocolsupply.repository.user;

import com.etone.protocolsupply.model.entity.user.ScutUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ScutUserRepository extends JpaRepository<ScutUser, Long>, JpaSpecificationExecutor<ScutUser> {

    @Query(value = "select * from scut_user  where xm=?1", nativeQuery = true)
    ScutUser findScutUserbyXM(String scutuser);


}
