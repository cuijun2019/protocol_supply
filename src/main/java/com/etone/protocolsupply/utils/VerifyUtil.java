package com.etone.protocolsupply.utils;

import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class VerifyUtil {

    @Autowired
    private PartnerInfoRepository partnerInfoRepository;

    @Scheduled(cron = "0 0 9 * * ?")//每天早上9点触发审核任务
    public void verifySupplier(){
        //每天去查询注册时间满24小时的供应商和代理商，将审核状态改成已审核
        partnerInfoRepository.updateByRegisterTime(new Date());

    }
}
