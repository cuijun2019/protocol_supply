package com.etone.protocolsupply.repository.user;

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

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "insert into role_permis (role_id,perm_id) values (?1,?2)", nativeQuery = true)
    void saveRolePermissions(Long role_id, Long perm_id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update roles set status=2 where role_id=?1", nativeQuery = true)
    void deleteByRoleId(long roleId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "delete from role_permis where role_id=?1", nativeQuery = true)
    void deleteRolePermissions(long roleId);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update roles set description=?1,status=?2 where role_id=?3", nativeQuery = true)
    void updateRole(String description, Integer status, Long id);

    @Query(value = "select * from roles where role_id=(select role_id from user_role ur where EXISTS(select 1 from users u where ur.user_id= u.id and u.username=?1))", nativeQuery = true)
    List<Role> findRoleByNextActor(String nextActor);
}
