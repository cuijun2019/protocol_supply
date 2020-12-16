package com.etone.protocolsupply.cas;

import com.etone.protocolsupply.model.dto.systemControl.UserDto;
import com.etone.protocolsupply.model.entity.user.PermissionComparator;
import com.etone.protocolsupply.model.entity.user.Permissions;
import com.etone.protocolsupply.model.entity.user.Role;
import com.etone.protocolsupply.model.entity.user.User;
import com.etone.protocolsupply.repository.procedure.BusiJbpmFlowRepository;
import com.etone.protocolsupply.service.security.JwtTokenUtil;
import com.etone.protocolsupply.service.system.UserService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Component
public class CasUtils {

	private static final Logger logger = LoggerFactory.getLogger(CasUtils.class);


	private static String serverUrlPrefix;


	private static String clientHostUrl;

	@Value("${cas.server-url-prefix}")
	public void setServerUrlPrefix(String urlPrefix){
		serverUrlPrefix = urlPrefix;
	}

	@Value("${cas.client-host-url}")
	public void setClientHostUrl(String hostUrl){
		clientHostUrl = hostUrl;
	}

	private static AuthenticationManager authenticationManager;

	private static JwtTokenUtil jwtTokenUtil;

    private static UserService userService;

	private static BusiJbpmFlowRepository busiJbpmFlowRepository;

	@Autowired
    public CasUtils(AuthenticationManager authenticationManager,JwtTokenUtil jwtTokenUtil,UserService userService,BusiJbpmFlowRepository busiJbpmFlowRepository){
	    CasUtils.authenticationManager = authenticationManager;
	    CasUtils.jwtTokenUtil = jwtTokenUtil;
	    CasUtils.userService = userService;
		CasUtils.busiJbpmFlowRepository = busiJbpmFlowRepository;
    }


	public static Map<String, String> validateTickets(String ticket, String casServerUrlPrefix, String clientHostUrl) throws Exception {

			// ticket校验请求header设置
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json;charset=utf-8");

			String userUrl = casServerUrlPrefix + "/proxyValidate?ticket=" + ticket
					+ "&service=" + clientHostUrl;
		    System.out.println(userUrl+"校验的url--------");

			// 执行用户信息获取请求
			String data = HttpClientUtil.get(userUrl, headers);
			System.out.println("返回的xml数据"+data);

			return getCasMap(data);

	}

	private static String encodeURL(String value) {
		try {
			/* 296 */ return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			try {
				/* 299 */ return URLEncoder.encode(value, "GBK");
			} catch (UnsupportedEncodingException e1) {
				/* 301 */ throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 解析cas服务器返回的xml信息
	 * @param strXML
	 * @return
	 */
	public static Map getCasMap(String strXML){

		Document doc = null;

		HashMap<String, String> returnMap = new HashMap<>();

		try {
			doc = DocumentHelper.parseText(strXML);

			Element root = doc.getRootElement();// 指向根节点

			Iterator it = root.elementIterator();

			while (it.hasNext()) {
				Element element = (Element) it.next();// 一个Item节点
				Iterator iterator = element.elementIterator();
				while (iterator.hasNext()){
					Element element1 = (Element)  iterator.next();
					if("user".equals(element1.getName())){
						returnMap.put("user",element1.getText());
					}
					Iterator iterator1 = element1.elementIterator();
					while (iterator1.hasNext()){
						Element element2 = (Element) iterator1.next();
						if("checkAliveTicket".equals(element2.getName())){
							returnMap.put("ticket",element2.getText());
						}
					}
				}
			}
			return returnMap;
		} catch (Exception e) {
			logger.error("解析CAS返回的XML文件异常",e.getMessage());
		}
		return null;
	}

	/**
	 * 校验ticket信息并获取用户信息
	 * @param request
	 * @param response
	 * @return
	 */
	public static void getUserInfo(HttpServletRequest request, HttpServletResponse response) {

		//校验票据
		String ticket = request.getParameter("ticket");

		//保存第一次的票据
        request.getSession().setAttribute("ticket",ticket);

		String username = "";

		try {
			System.out.println("开始校验ticket--------");
			Map<String, String> validateTickets = CasUtils.validateTickets(ticket, serverUrlPrefix,clientHostUrl);
			if(validateTickets.size() == 0){
				request.getSession().setAttribute("errorMessage","ticket校验异常");
			}
			username = validateTickets.get("user");
		} catch (Exception e) {
			logger.error("校验ticket异常",e.getMessage());
			request.getSession().setAttribute("errorMessage","ticket校验异常");

		}

		UserDto userDto = new UserDto();
		User user = userService.findUserByUsername(username);
		if(user!=null){
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
				logger.error("用户名或密码错误",e.getMessage());
				request.getSession().setAttribute("errorMessage","用户名或密码错误");
			}

			// Reload password post-security so we can generate the token
			//伪造token
			final String token = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
			userDto.setToken(token);
			//根据username查找领导人
			String leader=busiJbpmFlowRepository.getLeaderByuserName(username);
			userDto.setLeader(leader);
			//用户信息保存到token
			request.getSession().setAttribute("userSession",userDto);
		}
	}
}
