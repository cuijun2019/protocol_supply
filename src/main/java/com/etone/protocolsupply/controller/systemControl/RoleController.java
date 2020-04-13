package com.etone.protocolsupply.controller.systemControl;


import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.systemControl.RoleCollectionDto;
import com.etone.protocolsupply.model.dto.systemControl.RoleDto;
import com.etone.protocolsupply.model.entity.user.Permissions;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.service.system.PermissionService;
import com.etone.protocolsupply.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "${jwt.route.path}/role")
public class RoleController extends GenericController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;


    /**
     * 分页查询角色列表
     * @param currentPage 当前页码
     * @param pageSize    需要展示的条数
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getRoles(@Validated
                                  @RequestParam(value = "roleName", required = false) String roleName,
                                  @RequestParam(value = "statusSearch", required = false)  String statusSearch,
                                  @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                  HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<Role> specification = roleService.getWhereClause(roleName,statusSearch);
        Page<Role> page = roleService.findRoles(specification, pageable);

        RoleCollectionDto roleDtos = roleService.to(page, request);
        responseBuilder.data(roleDtos);
        return responseBuilder.build();
    }

    /**
     * 新增角色
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue saveUser(@Validated
                                  @RequestBody RoleDto roleDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        roleService.save(roleDto,this.getUser());
        responseBuilder.message("保存角色成功");
        return responseBuilder.build();
    }


    /**
     * 根据角色id删除角色
     * @param roleId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{roleId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue deleteUser(@PathVariable("roleId") String roleId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        roleService.delete(Long.parseLong(roleId));
        responseBuilder.message("删除角色成功");
        return responseBuilder.build();
    }

    /**
     * 根据角色id查询该角色所拥有的权限(一级二级权限)
     * @param roleId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getRolePermission/{roleId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getPermissionByRoleId(@PathVariable("roleId") String roleId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<Map<String,Object>> permissions = permissionService.getPermissionByRoleId(Long.parseLong(roleId));
        responseBuilder.data(permissions);
        return responseBuilder.build();
    }

    /**
     * 根据角色id和二级权限id查询该角色所拥有的三级权限
     * @param roleId  角色id
     * @param secondPermissionId  二级权限id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getThirdPermissionByRoleId/{roleId}/{secondPermissionId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getThirdPermissionByRoleId(@PathVariable("roleId") String roleId,
                                                    @PathVariable("secondPermissionId") String secondPermissionId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<Permissions> permissions = permissionService.getThirdPermissionByRoleId(roleId,secondPermissionId);
        responseBuilder.data(permissions);
        return responseBuilder.build();
    }

    /**
     * 查询所有一二级权限菜单
     */
    @ResponseBody
    @RequestMapping(value = "/getAllPermission",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getAllPermission() {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<Map<String,Object>> permissions =  permissionService.getAllPermission();
        responseBuilder.data(permissions);
        return responseBuilder.build();
    }


    /**
     * 根据二级权限id查询其对应的所有三级权限
     */
    @ResponseBody
    @RequestMapping(value = "/getThirdPermissionBySecondPermissionId/{permissionId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getThirdPermissionBySecondPermissionId(@PathVariable("permissionId") String permissionId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<Permissions> permissions =  permissionService.getThirdPermissionBySecondPermissionId(permissionId);
        responseBuilder.data(permissions);
        return responseBuilder.build();
    }


    /**
     * 编辑角色部分内容和对应的权限
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(
            value = "/updateRolePermissions",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateRolePermissions(@Validated
                                    @RequestBody RoleDto roleDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        roleService.updateRolePermissions(roleDto);
        responseBuilder.message("更新角色及权限成功");
        return responseBuilder.build();
    }

    /**
     * 查询所有角色
     */
    @ResponseBody
    @RequestMapping(value = "/getAllRoles",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getAllRoles() {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<Role> roleList = roleService.getAllRoles();
        responseBuilder.data(roleList);
        return responseBuilder.build();
    }


}
