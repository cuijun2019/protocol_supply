package com.etone.protocolsupply.cas;
//
import com.neusoft.cas.AuthenticationFilter;
import com.neusoft.cas.Cas20ProxyReceivingTicketValidationFilter;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Configuration
public class CasConfigure {

    @Value("${cas.server-url-prefix")
    private String serverUrlPrefix;
    @Value("${cas.server-login-url}")
    private String serverLoginUrl;
    @Value("${cas.client-host-url}")
    private String clientHostUrl;
    @Value("${cas.validation-type}")
    private String validationType;




   /* @Bean
    public FilterRegistrationBean CASValidationFilter(){
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new Cas20ProxyReceivingTicketValidationFilter());
        HashMap<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerUrlPrefix",serverUrlPrefix);
        initParameters.put("serverName",clientHostUrl);
        //initParameters.put("exclusions","/api/getCode");
        authenticationFilter.setInitParameters(initParameters);
        authenticationFilter.setOrder(2);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");// 设置匹配的url
        authenticationFilter.setUrlPatterns(urlPatterns);
        return authenticationFilter;
    }

    @Bean
    public FilterRegistrationBean CASAuthenticationFilter(){
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new AuthenticationFilter());
        HashMap<String, String> initParameters = new HashMap<>();
        initParameters.put("casServerLoginUrl",serverLoginUrl);
        initParameters.put("serverName",clientHostUrl);
        //initParameters.put("exclusions","/api/getCode");
        authenticationFilter.setInitParameters(initParameters);
        authenticationFilter.setOrder(1);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");// 设置匹配的url
        authenticationFilter.setUrlPatterns(urlPatterns);
        return authenticationFilter;
    }


    @Bean
    public FilterRegistrationBean casHttpServletRequestWrapperFilter(){
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new HttpServletRequestWrapperFilter());
        authenticationFilter.setOrder(3);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");// 设置匹配的url
        //authenticationFilter.addInitParameter("exclusions","/api/getCode");
        authenticationFilter.setUrlPatterns(urlPatterns);
        return authenticationFilter;
    }


    @Bean
    public FilterRegistrationBean SingleSignOutFilter(){
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new SingleSignOutFilter());
        authenticationFilter.setOrder(4);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/*");// 设置匹配的url
        authenticationFilter.setUrlPatterns(urlPatterns);
        //authenticationFilter.addInitParameter("exclusions","/api/getCode");
        return authenticationFilter;
    }
*/




}
