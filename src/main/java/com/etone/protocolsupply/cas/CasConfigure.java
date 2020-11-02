package com.etone.protocolsupply.cas;

import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
//@Configuration
//@Component
public class CasConfigure {

    @Value("${cas.server-url-prefix}")
    private String serverUrlPrefix;
    @Value("${cas.server-login-url}")
    private String serverLoginUrl;
    @Value("${cas.client-host-url}")
    private String clientHostUrl;
    @Value("${cas.validation-type}")
    private String validationType;
    @Value("${ignore-host-url}")
    private String ignoreHostUrl;

    @Bean
    public FilterRegistrationBean CASAuthenticationFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new AuthenticationFilter());

        // 设置匹配的url
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);

        HashMap<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerLoginUrl",serverLoginUrl);
        initParameters.put("serverName",clientHostUrl);
        initParameters.put("ignorePattern",ignoreHostUrl);
        registrationBean.setInitParameters(initParameters);
        //设置加载顺序
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean CASValidationFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new Cas20ProxyReceivingTicketValidationFilter());

        // 设置匹配的url
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);

        HashMap<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerUrlPrefix",serverUrlPrefix);
        initParameters.put("serverName",clientHostUrl);
        initParameters.put("ignorePattern",ignoreHostUrl);
        registrationBean.setInitParameters(initParameters);

        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean casHttpServletRequestWrapperFilter2(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HttpServletRequestWrapperFilter());

        // 设置匹配的url
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);

        HashMap<String, String> initParameters = new HashMap<>();
        initParameters.put("ignorePattern",ignoreHostUrl);
        registrationBean.setInitParameters(initParameters);

        registrationBean.setOrder(3);
        return registrationBean;
    }


    @Bean
    public FilterRegistrationBean SingleSignOutFilter2(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new SingleSignOutFilter());

        // 设置匹配的url
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);

        HashMap<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerUrlPrefix",serverUrlPrefix);
        initParameters.put("ignorePattern",ignoreHostUrl);
        registrationBean.setInitParameters(initParameters);

        //设置加载顺序
        registrationBean.setOrder(4);
        return registrationBean;
    }

}
