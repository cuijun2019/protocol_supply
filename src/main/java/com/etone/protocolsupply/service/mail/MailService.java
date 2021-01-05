package com.etone.protocolsupply.service.mail;

import com.etone.protocolsupply.model.entity.Attachment;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.AttachmentRepository;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Transactional(rollbackFor = Exception.class)
@Service
@PropertySource(value = {
        "classpath:myApplication.properties",
}, encoding = "utf-8")
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Value("${spring.mail.username}")
    private String host;

    @Value("${email.address}")
    private String email;

    @Resource
    private RedisUtil redisUtil;

    public Boolean sendMail(Map<String, String> jsonData) {
        User user = userRepository.findUserByCondition(jsonData.get("username"),jsonData.get("email"));
        if(user == null){
            return false;
        }
        //发送邮件
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(host);

        message.setTo(jsonData.get("email"));

        message.setSubject("密码重置验证码");

        //随机验证码
        String code = (int)((Math.random()*9+1)*100000)+"";

        message.setText("【"+code+"】,有效期2分钟");
        //存入redis设置过期时间
        redisUtil.set(jsonData.get("username"),code,120);

        try {

            mailSender.send(message);
            logger.info("测试邮件已发送。");

        } catch (Exception e) {
            logger.error("发送邮件时发生异常了！", e);
            return false;
        }
        return true;
    }

    public Boolean checkVerifyCode(Map<String, String> jsonData) {
        User user = userRepository.findByUsername(jsonData.get("username"));
        if(user == null){
            return false;
        }
        //校验验证码
        String code = (String)redisUtil.get(jsonData.get("username"));
        if(code == null){
            return false;
        }
        if(jsonData.get("code").equals(code)){
            return true;
        }
        return false;
    }

    /**
     * 发送加密附件的密码
     * @param attachmentId
     */
    public void sendAttachmentPassword(String attachmentId) {
        if(attachmentId.length()>0 && attachmentId.contains(",")){
            String[] attachmentIds = attachmentId.split(",");
            for (int i = 0; i < attachmentIds.length; i++) {
                Attachment attachment = attachmentRepository.sendAttachmentPassword(Integer.parseInt(attachmentIds[i]));
                if(attachment!=null){
                    sendEmail(attachment.getAttachName(),attachment.getPassword(),attachment.getProjectCode());
                }
            }
        }
    }

    //发送密码给密码保管人
    private boolean sendEmail(String zipFileName,String password,String projectCode) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(host);

        message.setTo(email);

        if(zipFileName.contains("采购合同")){
            message.setSubject("采购合同");
        }else {
            message.setSubject("成交通知书");
        }

        message.setText("项目编号:"+projectCode+"的"+zipFileName+"的密码为"+password+",请查收");

        try {

            mailSender.send(message);
            logger.info("测试邮件已发送。");

        } catch (Exception e) {
            logger.error("发送邮件时发生异常了！", e);
            return false;
        }
        return true;
    }
}
