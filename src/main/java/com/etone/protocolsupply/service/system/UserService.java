package com.etone.protocolsupply.service.system;

import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.exception.GlobalServiceException;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.systemControl.UserCollectionDto;
import com.etone.protocolsupply.model.dto.systemControl.UserDto;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.user.RoleRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.utils.BcryptCipher;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

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

    public Specification<User> getWhereClause(String isDelete,String username,String enabled) {
        return (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if(Strings.isNotBlank(username)){
                predicates.add(criteriaBuilder.equal(root.get("username").as(String.class),username));
            }
            if(Strings.isNotBlank(enabled)){
                if("true".equals(enabled)){
                    predicates.add(criteriaBuilder.equal(root.get("enabled").as(Boolean.class),true));
                }else if("false".equals(enabled)){
                    predicates.add(criteriaBuilder.equal(root.get("enabled").as(Boolean.class),false));
                }
            }
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

    public String save(UserDto userDto) {
        Date date = new Date();

        //先根据用户名查询是否已存在该用户
        User check = userRepository.findByUsername(userDto.getUsername());
        if(check!=null){
            return "该用户已经存在";
        }

        User user = new User();
        user.setCompany(userDto.getCompany());
        user.setCreateTime(date);
        user.setEmail(userDto.getEmail());
        user.setEnabled(userDto.getEnabled());
        user.setFullname(userDto.getFullname());
        user.setIsDelete(2);
        user.setPassword(BcryptCipher.Bcrypt(userDto.getPassword()).get("cipher"));
        user.setSex(userDto.getSex());
        user.setTelephone(userDto.getTelephone());
        user.setUpdateTime(date);
        user.setUsername(userDto.getUsername());
        user.setAttachment(null);

        User save = userRepository.save(user);

        List<Role> roles = userDto.getRoles();
        if(roles!=null && roles.size()>0){
            //新增用户角色关系
            for (int i = 0; i < roles.size(); i++) {
                roleRepository.addUserRole(save.getId(),roles.get(i).getId());
            }
        }
        return "保存用户成功";
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

        //新增用户角色关系,如果roleId为0，说明这个用户没有添加新角色
        if(!("0".equals(roleId))){
            String[] roleIds = roleId.split(",");
            if (roleIds.length>0){
                for (int i = 0; i < roleIds.length; i++) {
                    roleRepository.addUserRole(Long.parseLong(userId),Long.parseLong(roleIds[i]));
                }
            }
        }

    }

    //更新用户信息
    public void updateUser(UserDto userDto) {
        Date date = new Date();
        Long id = userDto.getId();
        @NotNull @Size(min = 4, max = 100) String password = userDto.getPassword();
        @NotNull String company = userDto.getCompany();
        @NotNull Date createTime = userDto.getCreateTime();
        @NotNull String email = userDto.getEmail();
        @NotNull Boolean enabled = userDto.getEnabled();
        @NotNull String sex = userDto.getSex();
        @NotNull @Size(min = 4, max = 50) String telephone = userDto.getTelephone();
        @NotNull String fullname = userDto.getFullname();

        userRepository.updateUser(company,createTime,email,enabled,sex,telephone,date,password,fullname,id);



        //更新用户的角色
        List<Role> roleList = userDto.getRoles();
        if(roleList!=null && roleList.size()>0){

            //先删除用户角色表中选中用户和角色的关系
            roleRepository.deleteByUserId(id);

            //新增用户角色关系
            for (int i = 0; i < roleList.size(); i++) {
                roleRepository.addUserRole(id,roleList.get(i).getId());
            }
        }
    }


    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Boolean updatePassword(String oldPassword, String newPassword, JwtUser user) {
        //当前登录用户名密码
        String username = user.getUsername();
        String userPassword = user.getPassword();
        //校验
        boolean matches = BCrypt.checkpw(oldPassword, userPassword);

        //更新密码
        Map<String, String> pwd = BcryptCipher.Bcrypt(newPassword);
        if(matches){
            userRepository.updatePassword(pwd.get("cipher"),username);
        }else {
            return false;
        }
        return true;
    }

    public List<String> getUserByRoleId(String roleId) {
        ArrayList<String> nameList = new ArrayList<>();
        List<User> userList = userRepository.getUserByRoleId(Long.parseLong(roleId));
        if(userList!=null && userList.size()>0){
            for (int i = 0; i < userList.size(); i++) {
                nameList.add(userList.get(i).getUsername());
            }
        }
        return nameList;
    }
}
