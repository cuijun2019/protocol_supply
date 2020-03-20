package com.etone.protocolsupply.repository;

import com.etone.protocolsupply.model.entity.Role;
import com.etone.protocolsupply.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Description //TODO
 * @Date 2018/12/2 下午5:11
 * @Author maozhihui
 * @Version V1.0
 **/
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    List<User> findAllByRoles(Role role);
}
