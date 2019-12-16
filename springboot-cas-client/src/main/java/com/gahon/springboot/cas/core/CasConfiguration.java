package com.gahon.springboot.cas.core;

import org.jasig.cas.client.boot.configuration.CasClientConfiguration;
import org.jasig.cas.client.boot.configuration.CasClientConfigurationProperties;
import org.jasig.cas.client.boot.configuration.CasClientConfigurer;
import org.jasig.cas.client.boot.configuration.EnableCasClient;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 添加单点登出支持，3.6.1以上版本应该不需要该类了
 *
 * @author Gahon
 * @date 2019/11/5
 */
@Configuration
@EnableCasClient
@ConditionalOnProperty(prefix = "cas-config", value = "enable", havingValue = "true")
public class CasConfiguration implements CasClientConfigurer {

    @Value("${cas-config.ignore-pattern: #{null}}")
    private String ignorePattern;

    @Value("${cas-config.ignore-url-pattern-type:#{null}}")
    private String ignoreUrlPatternType;

    /**
     * 配置登出过滤器
     * 通过配置order使得其在所有filter之前执行（可以在编码filter或跨域filter之后执行）
     */
    @Bean(name = "casSingleSignOutFilter")
    @ConditionalOnBean({CasClientConfiguration.class, CasClientConfigurationProperties.class})
    public FilterRegistrationBean casSingleSignOutFilter(CasClientConfigurationProperties configProps) {
        final FilterRegistrationBean singleSignOutFilter = new
                FilterRegistrationBean();
        singleSignOutFilter.setFilter(new SingleSignOutFilter());
        Map<String, String> initParameters = new HashMap<>(1);
        initParameters.put("casServerUrlPrefix",
                configProps.getServerUrlPrefix());
        singleSignOutFilter.setInitParameters(initParameters);
        singleSignOutFilter.setOrder(0);
        return singleSignOutFilter;
    }

    /**
     * 添加监听器
     *
     * @return
     */
    @Bean
    @ConditionalOnBean(CasClientConfiguration.class)
    public ServletListenerRegistrationBean<EventListener> casSingleSignOutListener() {
        ServletListenerRegistrationBean<EventListener> singleSignOutListener =
                new ServletListenerRegistrationBean<>();
        singleSignOutListener.setListener(new
                SingleSignOutHttpSessionListener());
        singleSignOutListener.setOrder(0);
        return singleSignOutListener;
    }

    /**
     * 配置路径排除方法
     */
    @Override
    public void configureAuthenticationFilter(FilterRegistrationBean
                                                      authenticationFilter) {
        if (!StringUtils.isEmpty(ignorePattern) && !StringUtils.isEmpty(ignoreUrlPatternType)) {
            // 配置不进行验证的路径
            authenticationFilter.getInitParameters().put("ignorePattern", ignorePattern);
            // 配置自定义路径匹配类或者使用cas自带的路径匹配
            authenticationFilter.getInitParameters().put("ignoreUrlPatternType",
                    ignoreUrlPatternType);
        }
    }

    /**
     * 添加全局属性配置， 注意该Filter的位置，必须在assertionThreadLocalFilter 后边。
     * 另外需要保证该filter在其他使用 {@link CasUtils} 的filter之前加载
     */
    @Bean(name = "contextThreadLocalFilter")
    public FilterRegistrationBean contextThreadLocalFilter() {
        FilterRegistrationBean contextThreadLocalFilter = new FilterRegistrationBean();
        contextThreadLocalFilter.setFilter(new ContextThreadLocalFilter());
        contextThreadLocalFilter.setOrder(5);
        contextThreadLocalFilter.setUrlPatterns(Collections.singleton("/*"));
        return contextThreadLocalFilter;
    }

    /**
     * 配置自动登录原系统的filter，保证该filter在contextThreadLocalFilter之后，
     * 同时在其他需要原系统登录的地方之前加载
     */
    @Bean(name = "autoAuthFilter")
    public FilterRegistrationBean autoAuthFilter() {
        FilterRegistrationBean autoAuthFilter = new FilterRegistrationBean();
        autoAuthFilter.setFilter(new AutoAuthFilter());
        autoAuthFilter.setOrder(6);
        autoAuthFilter.setUrlPatterns(Collections.singleton("/*"));
        return autoAuthFilter;
    }
}
