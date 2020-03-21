package com.etone.protocolsupply.controller;

import com.etone.protocolsupply.exception.AuthenticationException;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.LoginRequest;
import com.etone.protocolsupply.service.security.JwtTokenUtil;
import com.etone.protocolsupply.model.dto.ResponseValue;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.authentication.CachingUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Description jwt token的生成与刷新
 * @Date 2018/12/2 下午5:27
 * @Author maozhihui
 * @Version V1.0
 **/
@Slf4j
@RestController
public class LoginController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.auth.prefix}")
    private String authPrefix;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("cachingUserDetailsService")
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@Validated @RequestBody LoginRequest authenticationRequest) {

        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Reload password post-security so we can generate the token
        final String token = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());

        // Return the token
        return ResponseEntity.ok(ResponseValue.createBuilder().data(token).build());
    }

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getCreateTime())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(ResponseValue.createBuilder().data(refreshedToken).build());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    /**
     * Authenticates the user. If something is wrong, an {@link AuthenticationException} will be thrown
     */
    private Authentication authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Bad credentials!", e);
        }
    }

    @PostMapping(value = "/api/logout")
    public ResponseValue logout(@RequestHeader(value = "Authorization") String auth) {
        String username;
        if (auth != null && auth.startsWith(this.authPrefix)) {
            String authToken = auth.substring(this.authPrefix.length());
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
                if (this.userDetailsService instanceof CachingUserDetailsService) {
                    ((CachingUserDetailsService) this.userDetailsService).getUserCache().removeUserFromCache(username);
                    log.info("username {} logout success.", username);
                    return ResponseValue.createBuilder().build();
                }
            } catch (IllegalArgumentException e) {
                log.error("an error occured during getting username from token", e);
            } catch (ExpiredJwtException e) {
                log.warn("the token is expired and not valid anymore", e);
            }
        } else {
            log.error("Authorization {} is not valid.", auth);
        }
        return ResponseValue.createBuilder().code(GlobalExceptionCode.LOGOUT_ERROR.getCode())
                .message(GlobalExceptionCode.LOGOUT_ERROR.getCause()).build();
    }
}
