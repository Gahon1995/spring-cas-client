package com.gahon.springboot.cas.core.web;

import com.gahon.springboot.cas.core.profile.UserProfile;
import com.gahon.springboot.cas.core.util.CasUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Gahon
 * @date 2019/12/12
 */
@Controller
@RequestMapping("/cas")
public class CasController {

    @Value("${cas-config.logout-default-url:}")
    private String logoutDefaultUrl;

    @Value("${cas-config.local-logout:true}")
    private Boolean localLogout;

    @Value("${cas-config.central-logout:true}")
    private Boolean centralLogout;

    @Value("${cas.server-url-prefix}")
    private String serverUrlPrefix;

    @Value("${cas.client-host-url}")
    private String clientHostUrl;

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(final HttpServletRequest request) {
//        如果使用了shiro， 需要将下边这行取消注释，注销shiro自己的session
//        SecurityUtils.getSecurityManager().logout(SecurityUtils.getSubject());
        final HttpSession session = request.getSession();
//      默认将本地的session销毁， 保证session及时销毁
        session.invalidate();
        if (StringUtils.isEmpty(logoutDefaultUrl)) {
//            如果没有设置退出登录后跳转回来的地址，则使用项目地址
            if (StringUtils.isEmpty(clientHostUrl)) {
                clientHostUrl = "/";
            }
            logoutDefaultUrl = clientHostUrl;
        }
        String redirectUrl = logoutDefaultUrl;
        if (centralLogout) {
//            跳转到服务端进行中央退出登录, 其中回调地址为当前项目的 '/cas/login' ，
//            目的是为了保证前后端分离项目中cookie能够正确写入后端域名中
            redirectUrl = serverUrlPrefix + "/logout?service=" + clientHostUrl + "/cas/login";
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * 如果改了这个login的请求路径，请将logout中的redirect路径修改为争取的路径
     */
    @GetMapping("/login")
    public String login() {
        final UserProfile loginUserInfo = CasUtils.getLoginUserInfo();
        if (loginUserInfo == null) {
//            如果当前没有登录，重定向至cas server进行登录，然后跳回来
//            按理说不会进这个方法，因为这个已经被cas给拦截
            return "redirect:" + serverUrlPrefix + "/login?service=" + clientHostUrl;
        } else {
//            登录成功了，重定向至前端页面
            return "redirect:" + logoutDefaultUrl;
        }
    }

    /**
     * 获取当前登录的用户信息
     */
    @GetMapping("/userInfo")
    @ResponseBody
    public UserProfile info() {
        return CasUtils.getLoginUserInfo();
    }
}
