package com.etone.protocolsupply.controller.systemControl;


import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.systemControl.RoleCollectionDto;
import com.etone.protocolsupply.model.dto.systemControl.RoleDto;
import com.etone.protocolsupply.model.entity.user.Role;
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

@RestController
@RequestMapping(value = "${jwt.route.path}/role")
public class RoleController extends GenericController {

    @Autowired
    private RoleService roleService;


    /**
     * 分页查询角色列表
     * @param status    是否删除 1删除   2未删除
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
                                  @RequestParam(value = "status", required = false) String status,
                                  @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                  HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        Specification<Role> specification = roleService.getWhereClause(status);
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
        roleService.save(roleDto);
        responseBuilder.message("保存角色成功");
        return responseBuilder.build();
    }
}
