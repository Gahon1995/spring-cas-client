package com.gahon.springboot.cas.core.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Gahon
 * @date 2019/12/12
 */
@Controller
@RequestMapping("/cas")
public class CasLogoutController {

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
//        如果使用了shiro，但是没有将cas与shiro结合的话需要将下边这行取消注释，否则会报错
//        SecurityUtils.getSecurityManager().logout(SecurityUtils.getSubject());
        final HttpSession session = request.getSession();
//            将本地的session销毁
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
//            跳转到服务端进行中央退出登录
            redirectUrl = serverUrlPrefix + "/logout?service=" + logoutDefaultUrl;
        }
        return "redirect:" + redirectUrl;
    }
}
