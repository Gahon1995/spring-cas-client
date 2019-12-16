package com.gahon.springmvc.cas.core;

import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Gahon
 * @date 2019/12/12
 */
public class CasLogoutFilter implements Filter {

    private String logoutDefaultUrl;

    private Boolean centralLogout = true;

    private String serverUrlPrefix;

//    private String logoutPath;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        serverUrlPrefix = filterConfig.getInitParameter("serverUrlPrefix");
//        logoutPath = filterConfig.getInitParameter("logoutPath");
        logoutDefaultUrl = filterConfig.getInitParameter("logoutDefaultUrl");
        centralLogout = Boolean.valueOf(filterConfig.getInitParameter("centralLogout"));

        if (serverUrlPrefix == null) {
            throw new ServletException("serverUrlPrefix need to be set");
        }

//        if (logoutPath == null) {
//            throw new ServletException("logoutPath need to be set");
//        }

        if (logoutDefaultUrl == null) {
            throw new ServletException("logoutDefaultUrl need to be set");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        if (request.getServletPath().equals(logoutPath)) {
            final HttpSession session = request.getSession();
//            将本地的session销毁
            session.invalidate();
            if (StringUtils.isEmpty(logoutDefaultUrl)) {
//            如果没有设置退出登录后跳转回来的地址，则使用项目地址
                logoutDefaultUrl = "no logoutDefaultUrl set";
            }
            String redirectUrl = logoutDefaultUrl;
            if (centralLogout) {
//            跳转到服务端进行中央退出登录
                redirectUrl = serverUrlPrefix + "/logout?service=" + logoutDefaultUrl;
            }
            response.sendRedirect(redirectUrl);
//        } else {
//            filterChain.doFilter(servletRequest, servletResponse);
//        }
    }

    @Override
    public void destroy() {

    }
}
