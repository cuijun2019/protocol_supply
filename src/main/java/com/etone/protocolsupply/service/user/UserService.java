package com.etone.protocolsupply.service.user;

import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.systemControl.UserCollectionDto;
import com.etone.protocolsupply.model.dto.systemControl.UserDto;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.RoleRepository;
import com.etone.protocolsupply.repository.UserRepository;
import com.etone.protocolsupply.utils.PagingMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PagingMapper pagingMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Specification<User> getWhereClause(String isDelete) {
        return (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("isDelete").as(Long.class), isDelete));
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<User> findUsers(Specification<User> specification, Pageable pageable) {
        return userRepository.findAll(specification,pageable);
    }

    public UserCollectionDto to(Page<User> page, HttpServletRequest request) {
        UserCollectionDto userCollectionDto = new UserCollectionDto();
        pagingMapper.storeMappedInstanceBefore(page, userCollectionDto, request);
        UserDto userDto;
        for (User user : page) {
            userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            userCollectionDto.add(userDto);
        }
        return userCollectionDto;
    }

    public User save(UserDto userDto, JwtUser jwtUser) {
        Date date = new Date();
        String creator = jwtUser.getUsername();

        //TODO 密码加密

        User user = new User();
        user.setCompany(userDto.getCompany());
        user.setCreateTime(date);
        user.setEmail(userDto.getEmail());
        user.setEnabled(true);
        user.setFullname(creator);
        user.setIsDelete(2);
        user.setPassword(userDto.getPassword());
        user.setSex(userDto.getSex());
        user.setTelephone(userDto.getTelephone());
        user.setUpdateTime(date);
        user.setUsername(userDto.getUsername());
        user.setAttachment(null);

        return userRepository.save(user);
    }

    public User findOne(long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new GlobalServiceException(GlobalExceptionCode.NOT_FOUND_ERROR.getCode(), GlobalExceptionCode.NOT_FOUND_ERROR.getCause("通过用户ID"));
        }
    }

    public void delete(long userId) {
        userRepository.updateIsDelete(userId);
    }

    public List<Role> findRoleByUserId(long userId) {
        return roleRepository.findRoleByUserId(userId);
    }

    public void changeUserRole(String userId, String roleId) {
        //先删除用户角色表中选中用户和角色的关系
        roleRepository.deleteByUserId(Long.parseLong(userId));

        //新增用户角色关系
        String[] roleIds = roleId.split(",");
        if (roleIds.length>0){
            for (int i = 0; i < roleIds.length; i++) {
                roleRepository.addUserRole(Long.parseLong(userId),Long.parseLong(roleIds[i]));
            }
        }
    }
}
