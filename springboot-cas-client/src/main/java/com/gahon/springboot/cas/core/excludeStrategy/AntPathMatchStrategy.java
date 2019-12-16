package com.gahon.springboot.cas.core.excludeStrategy;

import org.jasig.cas.client.authentication.UrlPatternMatcherStrategy;
import org.springframework.util.AntPathMatcher;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 自定义路不需要登录的地址路径匹配类
 * <p>
 * 使用Spring自带的AntPathMatcher 做路径匹配
 *
 * @author Gahon
 * @date 2019/12/3
 */
public class AntPathMatchStrategy implements UrlPatternMatcherStrategy {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private String[] patterns;

    /**
     * Execute the match between the given pattern and the url
     *
     * @param url 请求全路径(包含http和?后边的数据    the request url typically with query strings included
     * @return true if match is successful
     */
    @Override
    public boolean matches(String url) {
        try {
//            通过URL转换获取当前路径的path路径（即不包含host和?后边的内容）
            final URI uri1 = new URI(url);
            final String path = uri1.getPath();
            if (patterns.length > 0) {
                for (String excludedPath : patterns) {
                    String uriPattern = excludedPath.trim();
                    if (pathMatcher.match(uriPattern.trim(), path)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The pattern against which the url is compared
     * 该 pattern 是通过 ignorePattern 注入的
     *
     * @param pattern 需要忽略的地址，在这里自定义为通过 ',' 分隔
     */
    @Override
    public void setPattern(String pattern) {
        if (pattern == null || "".equals(pattern)) {
            this.patterns = new String[0];
        } else {
            this.patterns = pattern.trim().split(",");
        }
    }
}
