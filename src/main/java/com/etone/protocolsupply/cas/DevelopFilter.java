package com.etone.protocolsupply.cas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class DevelopFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(DevelopFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest= (HttpServletRequest) request;
        System.out.println(httpServletRequest.getRequestURI()+"----");
        String ticket = httpServletRequest.getParameter("ticket");
        System.out.println("ticket是--"+ticket);

        //对比一次会话内的ticket值，防止每次都进行校验
        HttpSession session = httpServletRequest.getSession();
        String ticketSession = (String) session.getAttribute("ticket");
        System.out.println("ticketSession是--");
                                                                                              //一次会话内不进行多次ticket校验
        if(ticket != null && "/api/redirectCas".equals(httpServletRequest.getRequestURI())
                && ticket!=ticketSession){
            System.out.println("拦截到了/api/redirectCas，准备跳转到首页---------,先进行ticket校验");

            CasUtils.getUserInfo(httpServletRequest, (HttpServletResponse) response);

            //跳转
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendRedirect("http://211.66.32.79/#/login?redirect=%2F&scut=cas");
        }
        filterChain.doFilter(request,response);
    }



    @Override
    public void destroy() {

    }
}
