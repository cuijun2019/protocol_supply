package com.etone.protocolsupply.controller;

import com.etone.protocolsupply.model.dto.JwtUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author cuijun
 * @date 2018/12/28
 */
public abstract class GenericController {

    /**
     * 取得当前用户信息
     *
     * @return
     */
    public JwtUser getUser() {
        return (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
