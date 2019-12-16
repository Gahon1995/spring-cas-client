package com.gahon.springmvc.cas.core;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;

import javax.servlet.*;
import java.io.IOException;

/**
 * 通过该过滤器提供线程内已登录用户信息访问
 *
 * @author han
 */
public class ContextThreadLocalFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        try {
            final Assertion assertion = AssertionHolder.getAssertion();
            if (assertion != null && assertion.getPrincipal() != null) {
                //        从assertion中取得用户信息
                final AttributePrincipal principal = assertion.getPrincipal();
                CasUtils.setProfile(principal);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            //     清除用户信息
            CasUtils.clear();
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
