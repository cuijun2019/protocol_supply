package com.etone.protocolsupply.controller;

import com.etone.protocolsupply.cas.CasUtils;
import com.etone.protocolsupply.exception.AuthenticationException;
import com.etone.protocolsupply.exception.GlobalExceptionCode;
import com.etone.protocolsupply.model.dto.JwtUser;
import com.etone.protocolsupply.model.dto.LoginRequest;
import com.etone.protocolsupply.model.dto.ResponseValue;
import com.etone.protocolsupply.model.dto.systemControl.UserDto;
import com.etone.protocolsupply.model.entity.supplier.PartnerInfo;
import com.etone.protocolsupply.model.entity.user.PermissionComparator;
import com.etone.protocolsupply.model.entity.user.Permissions;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
import com.etone.protocolsupply.repository.supplier.PartnerInfoRepository;
import com.etone.protocolsupply.service.security.JwtTokenUtil;
import com.etone.protocolsupply.service.system.ScutUserService;
import com.etone.protocolsupply.service.system.UserService;
import com.etone.protocolsupply.utils.RedisUtil;
import com.etone.protocolsupply.utils.VerifyCodeUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @Description jwt token的生成与刷新
 * @Date 2018/12/2 下午5:27
 * @Author maozhihui
 * @Version V1.0
 **/
@Slf4j
@RestController
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${cas.server-url-prefix}")
    private String serverUrlPrefix;

    @Value("${cas.client-host-url}")
    private String clientHostUrl;

    @Value("${jwt.auth.prefix}")
    private String authPrefix;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("cachingUserDetailsService")
    private UserDetailsService userDetailsService;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private PartnerInfoRepository partnerInfoRepository;

    @Autowired
    private CasUtils casUtils;

    @Autowired
    private BusiJbpmFlowRepository busiJbpmFlowRepository;

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@Validated @RequestBody LoginRequest authenticationRequest,HttpServletRequest request) {

        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();
        String code = authenticationRequest.getCode();
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        //check code
        String code_session = (String) redisUtil.get(request.getSession().getId());
        if(!code_session.equalsIgnoreCase(code)){
            return ResponseEntity.ok(ResponseValue.createBuilder().message("验证码错误或验证码已过期").build());
        }

        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            logger.error("用户名或密码错误",e);
            return ResponseEntity.ok(ResponseValue.createBuilder().message("用户名或密码错误").build());
        }

        // Reload password post-security so we can generate the token
        final String token = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());

        UserDto userDto = new UserDto();
        User user = userService.findUserByUsername(username);
        List<Role> roles = user.getRoles();
        //判断要登录的用户身份如果是供应商或者代理商，未通过自动审核的暂时不准登录
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            if(role.getId()==1 || role.getId()==2){
                PartnerInfo partnerInfo = partnerInfoRepository.findById(user.getPartnerInfo().getPartnerId()).get();
                if(partnerInfo.getAuthStatus()==2){
                    return ResponseEntity.ok(ResponseValue.createBuilder().message("当前登录用户未通过审核").build());
                }
            }
            Set<Permissions> permissions = role.getPermissions();
            TreeSet<Permissions> tree = new TreeSet<>(new PermissionComparator());
            tree.addAll(permissions);
            roles.get(i).setPermissions(tree);
        }
        BeanUtils.copyProperties(user, userDto);
        userDto.setToken(token);
        //根据username查找领导人
        String leader=busiJbpmFlowRepository.getLeaderByuserName(username);
        userDto.setLeader(leader);

        // Return the token
        return ResponseEntity.ok(ResponseValue.createBuilder().data(userDto).build());
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
     * Authenticates the system. If something is wrong, an {@link AuthenticationException} will be thrown
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
    public ResponseValue logout(@RequestHeader(value = "Authorization") String auth, HttpServletResponse response, HttpSession session) {
        String username;
        if (auth != null && auth.startsWith(this.authPrefix)) {
            String authToken = auth.substring(this.authPrefix.length());
            try {
                session.invalidate();
                Cookie newCookie=new Cookie("JSESSIONID",null);
                newCookie.setMaxAge(0);
                response.addCookie(newCookie);

                username = jwtTokenUtil.getUsernameFromToken(authToken);
                if (this.userDetailsService instanceof CachingUserDetailsService) {
                    ((CachingUserDetailsService) this.userDetailsService).getUserCache().removeUserFromCache(username);
                    log.info("username {} logout success.", username);
                    return ResponseValue.createBuilder().data("").build();
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

    @GetMapping(value = "/api/getCode")
    public void getYzm(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpg");

            //生成随机字串
            String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
            log.info("verifyCode:{}", verifyCode);
            //存入会话session
            //HttpSession session = request.getSession(true);
            //session.setAttribute("_code", verifyCode.toLowerCase());
            //存入redis

            redisUtil.set(request.getSession().getId(),verifyCode,120);
            //生成图片
            int w = 146, h = 33;
            VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch (Exception e) {
            logger.error("生成验证码图片异常",e);
        }
    }


    @RequestMapping(value = "/api/redirectCas", method = RequestMethod.GET)
    public void redirectCas(HttpServletRequest request,HttpServletResponse response) {

        /*//校验票据
        String ticket = request.getParameter("ticket");

        String username = "";

        try {
            System.out.println("重定向到/api/redirectCas方法了--------");
            Map<String, String> validateTickets = CasUtils.validateTickets(ticket, serverUrlPrefix,clientHostUrl);
            if(validateTickets.size() == 0){
                return ResponseEntity.ok(ResponseValue.createBuilder().message("用户登陆信息校验异常").build());
            }
            username = validateTickets.get("user");

        } catch (Exception e) {
            logger.error("校验ticket异常",e);
            return ResponseEntity.ok(ResponseValue.createBuilder().message("用户登陆信息校验异常").build());
        }


        UserDto userDto = new UserDto();
        User user = userService.findUserByUsername(username);

        //身份权限处理
        List<Role> roles = user.getRoles();
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            Set<Permissions> permissions = role.getPermissions();
            TreeSet<Permissions> tree = new TreeSet<>(new PermissionComparator());
            tree.addAll(permissions);
            roles.get(i).setPermissions(tree);
        }
        BeanUtils.copyProperties(user, userDto);

        Authentication authentication = null;
        try {
            //认证登陆
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, "123456"));
        } catch (Exception e) {
            logger.error("用户名或密码错误",e);
            return ResponseEntity.ok(ResponseValue.createBuilder().message("用户名或密码错误").build());
        }

        // Reload password post-security so we can generate the token
        //伪造token
        final String token = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        userDto.setToken(token);

        // Return the token
        return ResponseEntity.ok(ResponseValue.createBuilder().data(userDto).build());*/
    }

    @RequestMapping(value = "/api/redirectCasForward", method = RequestMethod.GET)
    public ResponseEntity<?> redirectCasForward(HttpServletRequest request,HttpServletResponse response) {

        if(request.getSession().getAttribute("userSession") == null){
            return ResponseEntity.ok(ResponseValue.createBuilder().message("用户未成功登陆").build());
        }
        UserDto userDto = (UserDto) request.getSession().getAttribute("userSession");
        // Return the token
        return ResponseEntity.ok(ResponseValue.createBuilder().data(userDto).build());
    }

    @RequestMapping(value = "/api/toLoginPage", method = RequestMethod.GET)
    public void toLoginPage(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

        //casUtils.getUserInfo(request, response);

        request.getRequestDispatcher(clientHostUrl+"#/login?redirect=%2F&scut=cas").forward(request,response);
    }
}
