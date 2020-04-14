package com.etone.protocolsupply.repository.user;

import com.etone.protocolsupply.model.entity.user.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permissions, Long>, JpaSpecificationExecutor<Permissions> {

    @Query(value = "select * from permissions where parent_perm_id=?1", nativeQuery = true)
    List<Permissions> findByPermissionId(long permissionId);

    @Query(value = "select * from permissions p where exists (select 1 from role_permis rp, roles r where rp.role_id = r.role_id and rp.perm_id = p.perm_id and r.role_id =?1)", nativeQuery = true)
    List<Permissions> getPermissionByRoleId(long roleId);
}
