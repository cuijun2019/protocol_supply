package com.etone.protocolsupply.controller.mail;

import com.etone.protocolsupply.controller.GenericController;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.service.mail.MailService;
import com.etone.protocolsupply.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "${jwt.route.path}/mail")
public class MailController extends GenericController {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    /**
     * 忘记密码发送邮件(校验用户名是否存在，存在则发送)
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue sendMail(@Validated
                                  @RequestBody Map<String,String> jsonData) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Boolean send = mailService.sendMail(jsonData);
        if(send){
            responseBuilder.message("验证码已发送");
        }else {
            responseBuilder.message("用户名不存在");
        }
        return responseBuilder.build();
    }


    /**
     * 校验验证码
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "checkVerifyCode",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseValue checkVerifyCode(@Validated
                                  @RequestBody Map<String,String> jsonData) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();
        Boolean send = mailService.checkVerifyCode(jsonData);
        if(send){
            responseBuilder.code(100);
            responseBuilder.message("校验通过");
        }else {
            responseBuilder.code(233);
            responseBuilder.message("验证码已过期或用户名错误");
        }
        return responseBuilder.build();
    }


    /**
     * 修改密码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updatePassword",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseValue updatePassword(@RequestBody Map<String,String> jsonData) {
        ResponseValue.ResponseBuilder responseBuilder = ResponseValue.createBuilder();

        Boolean result = userService.updatePasswordByMail(jsonData.get("newPassword"),jsonData.get("username"));
        if(result){
            responseBuilder.message("密码重置成功");
        }else {
            responseBuilder.code(404);
            responseBuilder.message("密码修改失败");
        }
        return responseBuilder.build();
    }
}
