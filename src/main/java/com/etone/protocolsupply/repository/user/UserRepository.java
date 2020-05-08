package com.etone.protocolsupply.repository.user;

import com.etone.protocolsupply.model.entity.supplier.ContactInfo;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description //TODO
 * @Date 2018/12/2 下午5:11
 * @Author maozhihui
 * @Version V1.0
 **/
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);

    List<User> findAllByRoles(Role role);

    Page<User> findAll(Specification<User> specification, Pageable pageable);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update users set is_delete=1 where id=?1", nativeQuery = true)
    void updateIsDelete(long userId);



    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update users set company=?1,create_time=?2,email=?3,enabled=?4,sex=?5,telephone=?6,update_time=?7,password=?8,fullname=?9 where id=?10", nativeQuery = true)
    void updateUser(String company, Date createTime, String email, Boolean enabled, String sex, String telephone, Date date, String password,String fullname, Long id);

    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update users set password=?1 where username=?2", nativeQuery = true)
    void updatePassword(String encode, String username);

    @Query(value = "select u.*,r.role_id from roles r inner join user_role ur on ur.role_id=r.role_id inner join users u on ur.user_id=u.id where r.role_id in (:roleIds)", nativeQuery = true)
    List<Map> getUserByRoleId(@Param("roleIds") List<Long> roleIds);

    @Query(value = "select ur.role_id from users u LEFT join user_role ur on u.id=ur.user_id where u.username=?1", nativeQuery = true)
    Long findRoleIdByUsername(String username);

    @Query(value = "select * from users  where username=?1 and email=?2", nativeQuery = true)
    User findUserByCondition(String username, String email);

    @Query(value = "select * from users  where partner_id=?1", nativeQuery = true)
    User findByPartnerId(long partnerId);
}
