package com.etone.protocolsupply.cas;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CasfilterConfiguration {

    @Bean
    public FilterRegistrationBean developFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new DevelopFilter());
        registration.addUrlPatterns("/*");// 配置过滤路径
        registration.addInitParameter("exclusions","/api/getCode");
        registration.setName("developFilter");// 设置filter名称
        registration.setOrder(1);// 请求中过滤器执行的先后顺序，值越小越先执行
        return registration;
    }

}
