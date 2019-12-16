package com.gahon.springmvc.cas.core.filter;


import com.gahon.springmvc.cas.core.profile.UserProfile;
import com.gahon.springmvc.cas.core.util.CasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * 该Filter用来作为Cas用户系统与原有用户系统的衔接，具体使用方式见doFilter中的注释
 *
 * @author Gahon
 * @date 2019/12/16
 */
public class AutoAuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AutoAuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        先获取通过cas登录的用户信息
        final UserProfile userInfo = CasUtils.getLoginUserInfo();
        if (userInfo != null) {
//            通过cas登录了，获取得到当前用户
            log.debug("login-user: " + userInfo.toString());
//            判断原系统是否登录，没登录的话做登录处理（通过session中的某些字段判断啥的)
//            如果不判断原系统是否登录的话，会导致每次都会去做一次登录操作，使得效率较低。
//            当然必须进一步判断当前登录的用户和cas登录的用户是否一致，不一致的话进行用户切换

//            建议原系统用户部分新增一个字段，用来与cas用户系统的id绑定
//            然后在这个地方通过id去数据库中查询得到原系统的用户名密码，然后在做登录逻辑登录原系统
//            当通过cas-id没找到对应的用户时，应该利用cas的信息生成一个新用户，跳到一个注册界面进行新用户注册绑定到cas用户
//            如果在这个地方登陆了原系统，那么请注意在退出登录的时候也必须做退出原系统的操作，否则有可能导致下次使用其他用户登录后用户信息还是之前登录的用户

        } else {
//            没有通过cas登录，说明该请求路径没有被cas拦截，说明原系统也不需要登录，不做处理
            log.debug("用户未登录");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
