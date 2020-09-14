package com.etone.protocolsupply.service.mail;

import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.user.UserRepository;
import com.etone.protocolsupply.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Transactional(rollbackFor = Exception.class)
@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String host;

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
}
