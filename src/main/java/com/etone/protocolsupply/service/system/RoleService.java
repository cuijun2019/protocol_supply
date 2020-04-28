package com.etone.protocolsupply.service.system;

import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.systemControl.RoleCollectionDto;
import com.etone.protocolsupply.model.dto.systemControl.RoleDto;
import com.etone.protocolsupply.model.entity.user.Permissions;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.repository.user.RoleRepository;
import com.etone.protocolsupply.utils.PagingMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service
public class RoleService {

    @Autowired
    private PagingMapper pagingMapper;

    @Autowired
    private RoleRepository roleRepository;

    public Specification<Role> getWhereClause(String roleName,String statusSearch) {
        return (Specification<Role>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if(Strings.isNotBlank(roleName)){
                predicates.add(criteriaBuilder.equal(root.get("name").as(String.class),roleName));
            }
            if(Strings.isNotBlank(statusSearch)){
                predicates.add(criteriaBuilder.equal(root.get("status").as(String.class),statusSearch));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            return criteriaQuery.where(predicates.toArray(pre)).getRestriction();
        };
    }

    public Page<Role> findRoles(Specification<Role> specification, Pageable pageable) {
        return roleRepository.findAll(specification,pageable);
    }

    public RoleCollectionDto to(Page<Role> page, HttpServletRequest request) {
        RoleCollectionDto roleCollectionDto = new RoleCollectionDto();
        pagingMapper.storeMappedInstanceBefore(page,roleCollectionDto,request);
        RoleDto roleDto;
        for(Role role :page){
            roleDto = new RoleDto();
            BeanUtils.copyProperties(role,roleDto);
            roleCollectionDto.add(roleDto);
        }
        return roleCollectionDto;
    }

    public void save(RoleDto roleDto, JwtUser user) {
        Role role = new Role();
        String creator = user.getUsername();

        role.setCreateTime(new Date());
        role.setDescription(roleDto.getDescription());
        role.setName(roleDto.getName());
        role.setStatus(roleDto.getStatus());
        role.setCreator(creator);

        Role save = roleRepository.save(role);

        //保存角色对应的权限
        Set<Permissions> permissions = roleDto.getPermissions();
        if(permissions !=null && permissions.size()>0){
            for(Permissions permission:permissions){
                roleRepository.saveRolePermissions(save.getId(),permission.getPermId());
            }
        }
    }


    public void delete(long roleId) {
        //逻辑删除角色表的角色
        roleRepository.deleteByRoleId(roleId);

        //删除角色权限中间表中维护的关系
        roleRepository.deleteRolePermissions(roleId);
    }


    public void updateRolePermissions(RoleDto roleDto) {
        //更新角色部分内容(激活状态和描述)
        roleRepository.updateRole(roleDto.getDescription(),roleDto.getStatus(),roleDto.getId());

        //先删除角色权限关系表里维护的角色关系
        Set<Permissions> permissions = roleDto.getPermissions();


        //重新插入编辑后的角色权限关系
        if(permissions!=null && permissions.size()>0){

            //删除角色权限中间表中维护的关系
            roleRepository.deleteRolePermissions(roleDto.getId());

            for(Permissions permission:permissions){
                roleRepository.saveRolePermissions(roleDto.getId(),permission.getPermId());
            }
        }
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

}
