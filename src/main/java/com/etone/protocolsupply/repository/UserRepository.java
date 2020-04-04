package com.etone.protocolsupply.repository;

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
    @Query(value = "update users set company=?1,create_time=?2,email=?3,enabled=?4,fullname=?5,sex=?6,telephone=?7 where id=?8", nativeQuery = true)
    void updateUser(String company, Date createTime, String email, Boolean enabled, String fullname, String sex, String telephone, Long id);

}
