package com.gahon.springboot.cas.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 全局跨域请求头设置
 *
 * @author Gahon
 * @date 2019/12/18
 */
@Component
@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
@Order(-10)
public class CorsFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        logger.info(req.getRemoteHost() + " 使用" + request.getMethod() + "方法 访问 " + request.getRequestURI());
        HttpServletResponse response = (HttpServletResponse) res;
        //		配置不允许缓存，允许缓存可能会导致单点登录失败
        response.setHeader("Cache-Control", "no-cache");

        if (!StringUtils.isEmpty(request.getHeader("Origin"))) {
            //配置允许跨域的域为origin字段的域，否则无法跨域时无法传递cookie
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        } else {
            //没有origin头，配置为*，（配置为*时前端无法传递cookie）
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Methods", "POST, GET,OPTIONS,DELETE,PUT");
        response.setHeader("Access-Control-Max-Age", "1800");
//		Allow-Headers， 如果*不行，则需要将request中的request-Allow-Headers中的字段全加进来
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers") + ",*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
//		OPTIONS请求直接返回
        if ("OPTIONS".equals(request.getMethod())) {
            return;
        }
        chain.doFilter(req, response);
    }

    @Override
    public void destroy() {}

}
