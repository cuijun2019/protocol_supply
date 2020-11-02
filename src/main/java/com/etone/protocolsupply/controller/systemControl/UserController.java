package com.etone.protocolsupply.controller.systemControl;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.systemControl.UserCollectionDto;
import com.etone.protocolsupply.model.dto.systemControl.UserDto;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.service.system.UserService;
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
@RequestMapping(value = "${jwt.route.path}/user")
public class UserController extends GenericController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     * @param isDelete    是否删除 1删除   2未删除
     * @param currentPage 当前页码
     * @param pageSize    需要展示的条数
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getUsers(@Validated
                                   @RequestParam(value = "username", required = false) String username,
                                   @RequestParam(value = "enabled", required = false)  String enabled,
                                   @RequestParam(value = "isDelete", required = false) String isDelete,
                                   @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                   HttpServletRequest request) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, sort);
        //Specification<User> specification = userService.getWhereClause(isDelete,username,enabled);
        int enable = 1;
        if(enabled!=null){
            enable = (enabled.equals("true")?1:0);
        }
        Page<User> page = userService.findUsers(username,enable, pageable);

        UserCollectionDto userDtos = userService.to(page, request);
        responseBuilder.data(userDtos);

        return responseBuilder.build();
    }

    /**
     * 新增用户
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseValue saveUser(@Validated
                                   @RequestBody UserDto userDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        String save = userService.save(userDto);
        if(save.equals("该用户已经存在")){
            responseBuilder.message("该用户已经存在");
            responseBuilder.code(233);
        }else {
            responseBuilder.message("保存用户成功");
        }
        return responseBuilder.build();
    }


    /**
     * 根据用户id查询用户详情
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{userId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue getUser(@PathVariable("userId") String userId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        User user = userService.findOne(Long.parseLong(userId));
        responseBuilder.data(user);

        return responseBuilder.build();
    }



    /**
     * 根据用户id删除用户
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{userId}",
            method = RequestMethod.DELETE,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue deleteUser(@PathVariable("userId") String userId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        userService.delete(Long.parseLong(userId));
        responseBuilder.message("删除用户成功");
        return responseBuilder.build();
    }


    /**
     * 先查询该用户所拥有的角色
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserRole/{userId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getRole(@PathVariable("userId") String userId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        List<Role> roles = userService.findRoleByUserId(Long.parseLong(userId));
        responseBuilder.data(roles);
        return responseBuilder.build();
    }


    /**
     * 修改用户的角色
     * @param roleId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/changeUserRole/{userId}/{roleId}",
            method = RequestMethod.GET,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue changeUserRole(@PathVariable("userId") String userId,
                                        @PathVariable("roleId") String roleId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        userService.changeUserRole(userId,roleId);
        responseBuilder.message("修改用户角色成功");
        return responseBuilder.build();
    }

    /**
     * 更新用户信息
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(
            value = "/updateUser",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updateUser(@Validated
                                    @RequestBody UserDto userDto) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        userService.updateUser(userDto);
        responseBuilder.message("更新用户信息成功");
        return responseBuilder.build();
    }


    /**
     * 根据角色id查询对应的所有用户
     * @param roleId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserByRoleId",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue getUserByRoleId(@RequestBody(required = false) List<Long> roleId) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Map<String,String> userList = userService.getUserByRoleId(roleId);
        responseBuilder.data(userList);
        return responseBuilder.build();
    }
}
