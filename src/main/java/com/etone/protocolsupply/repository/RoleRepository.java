package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    @Query(value = "select * from roles r where exists (select 1 from user_role ur, users u where ur.role_id = r.role_id and ur.user_id = u.id and u.id =?1)", nativeQuery = true)
    List<Role> findRoleByUserId(long userId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from user_role where user_id=?1", nativeQuery = true)
    void deleteByUserId(Long userId);


    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "insert into user_role (user_id,role_id) values (?1,?2)", nativeQuery = true)
    void addUserRole(Long userId, Long roleId);
}
