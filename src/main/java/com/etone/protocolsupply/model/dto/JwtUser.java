package com.etone.protocolsupply.model.dto;

import com.etone.protocolsupply.model.entity.Attachment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * @Description 用户信息
 * @Date 2018/12/2 下午5:04
 * @Author maozhihui
 * @Version V1.0
 **/
public class JwtUser implements UserDetails {

    private final Long                                   id;
    private final String                                 username;
    private final String                                 fullname;
    private final String                                 password;
    private final String                                 telephone;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean                                enabled;
    private final Date                                   createTime;
    private final Date                                   updateTime;
    private final Attachment                             attachment;

    public JwtUser(
            Long id,
            String username,
            String fullname,
            String telephone,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            boolean enabled,
            Date createTime,
            Date updateTime,
            Attachment attachment
    ) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.telephone = telephone;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.attachment = attachment;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getFullname() {
        return fullname;
    }

    public String getTelephone() {
        return telephone;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @JsonIgnore
    public Date getCreateTime() {
        return createTime;
    }

    @JsonIgnore
    public Date getUpdateTime() {
        return updateTime;
    }

    @JsonIgnore
    public Attachment getAttachment() {
        return attachment;
    }
}
