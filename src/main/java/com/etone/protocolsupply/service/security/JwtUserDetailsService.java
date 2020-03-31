package com.etone.protocolsupply.service.security;

import com.etone.protocolsupply.constant.Constant;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description //TODO
 * @Date 2018/12/2 下午5:14
 * @Author maozhihui
 * @Version V1.0
 **/
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JwtUserFactory.create(user);
        }
    }

    public User save(User user) {
        Date date = new Date();
        user.setPassword(Constant.DEFAULT_PASSWORD);
        user.setEnabled(true);
        user.setCreateTime(date);
        user.setUpdateTime(date);

        return userRepository.save(user);
    }
}
