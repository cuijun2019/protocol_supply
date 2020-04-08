package com.etone.protocolsupply.service.system;

import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.systemControl.RoleCollectionDto;
import com.etone.protocolsupply.model.dto.systemControl.RoleDto;
import com.etone.protocolsupply.model.dto.systemControl.UserDto;
import com.etone.protocolsupply.model.entity.user.Permissions;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.RoleRepository;
import com.etone.protocolsupply.utils.PagingMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.soap.SOAPBinding;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Transactional(rollbackFor = Exception.class)
@Service
public class RoleService {

    @Autowired
    private PagingMapper pagingMapper;

    @Autowired
    private RoleRepository roleRepository;

    public Specification<Role> getWhereClause(String status) {
        return (Specification<Role>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status").as(Long.class), status));
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
        String username = user.getUsername();

        role.setCreateTime(new Date());
        role.setDescription(roleDto.getDescription());
        role.setName(roleDto.getName());
        role.setStatus(1);

        Role save = roleRepository.save(role);

        //保存角色对应的权限
        Set<Permissions> permissions = roleDto.getPermissions();
        if(permissions !=null && permissions.size()>0){
            for(Permissions permission:permissions){
                roleRepository.saveRolePermissions(save.getId(),permission.getPermId());
            }
        }
    }
}
