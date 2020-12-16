package com.etone.protocolsupply.utils;

import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class VerifyUtil {

    @Autowired
    private PartnerInfoRepository partnerInfoRepository;

    //@Scheduled(cron = "0 0 * * * ?")//每小时审核
    //@Scheduled(cron = "0 */1 * * * ?")//每分钟--测试
    public void verifySupplier(){
        //每天去查询注册时间满24小时的供应商和代理商，将审核状态改成已审核
        List<PartnerInfo> partnerInfoList = partnerInfoRepository.findByAuthStatus();
        if(partnerInfoList!=null && partnerInfoList.size()>0){
            for (int i = 0; i < partnerInfoList.size(); i++) {

                Date registerTime = partnerInfoList.get(i).getRegisterTime();
                Date currentTime = new Date();

                long time = currentTime.getTime() - registerTime.getTime();

                double hours = time * 1.0 / (1000 * 60 * 60);

                if(hours>=24){
                    System.out.println("更新了----");
                    partnerInfoRepository.updateByRegisterTime(currentTime,partnerInfoList.get(i).getPartnerId());
                }
            }
        }
    }
}
