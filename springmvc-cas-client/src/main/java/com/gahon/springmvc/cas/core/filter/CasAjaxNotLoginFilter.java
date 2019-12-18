package com.gahon.springmvc.cas.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gahon
 * @date 2019/12/18
 */
public class CasAjaxNotLoginFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CasAjaxNotLoginFilter.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String clientHostUrl;

    @Override
    public void init(FilterConfig filterConfig) {
        clientHostUrl = filterConfig.getInitParameter("clientHostUrl");
        if (clientHostUrl == null) {
            clientHostUrl = "/";
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        final HttpSession session = request.getSession(false);
         final Assertion assertion = session != null ? (Assertion) session.getAttribute(AuthenticationFilter.CONST_CAS_ASSERTION) : null;
        if (assertion == null  && isAjax(request)) {
            log.debug("当前未登录，返回未登录响应");
            Map<String, Object> result = new HashMap<>(3);
            result.put("code", 5000);
            result.put("msg", "当前未登录，请重新登录");
//            将需要跳转的地址设置到data字段，
            result.put("data", clientHostUrl);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().println(objectMapper.writeValueAsString(result));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    /**
     * 判断当前请求是否是ajax请求
     * @param request 请求
     * @return 该请求是否是ajax请求
     */
    public boolean isAjax(HttpServletRequest request) {
        final boolean xmlHttpRequest = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        final boolean hasDynamicAjaxParameter = Boolean.TRUE.toString()
                                                            .equalsIgnoreCase(request.getHeader("is_ajax_request"));
        final boolean hasDynamicAjaxHeader = Boolean.TRUE.toString()
                                                         .equalsIgnoreCase(request.getHeader("is_ajax_request"));
        return xmlHttpRequest || hasDynamicAjaxParameter || hasDynamicAjaxHeader;
    }
}
