package com.etone.protocolsupply.cas;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyCasConfigFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // 设置重定向参数
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //servletRequest.get
        filterChain.doFilter(servletRequest,response);
    }
}
