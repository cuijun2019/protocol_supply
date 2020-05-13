package com.etone.protocolsupply.init;

import com.etone.protocolsupply.service.security.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.authentication.CachingUserDetailsService;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;

/**
 * @Description
 * 1,引入带缓存的UserDetailsService,缓存接口为本地缓存
 * 2，为避免产生循环依赖，单独拿出来
 * @Date 2019/1/9 下午3:49
 * @Author maozhihui
 * @Version V1.0
 **/
@Slf4j
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private UserCache userCache(){
        try {
            Cache cache = new ConcurrentMapCache("securityUsers");
            SpringCacheBasedUserCache basedUserCache = new SpringCacheBasedUserCache(cache);
            return basedUserCache;
        } catch (Exception e){
            log.error("create system cache failed {}.",e.getMessage());
        }
        return null;
    }

    @Bean(name = "cachingUserDetailsService")
    public UserDetailsService getUserDetailsService(){
        Constructor<CachingUserDetailsService> ctor = null;
        try {
            ctor = CachingUserDetailsService.class.getDeclaredConstructor(UserDetailsService.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Assert.notNull(ctor, "CachingUserDetailsService constructor is null");
        ctor.setAccessible(true);

        CachingUserDetailsService cachingUserDetailsService = BeanUtils.instantiateClass(ctor, jwtUserDetailsService);
        cachingUserDetailsService.setUserCache(userCache());
        return cachingUserDetailsService;
    }
}
