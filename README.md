# CAS 单点登录集成配置教程

***注: 如果cas server与cas client在同一个host或ip上会导致单点登出失效***

## 一、 SpringBoot 集成

### 1. 引入依赖

#### ①	maven方式

```xml
<!--cas的客户端 -->
<dependency>
    <groupId>org.jasig.cas.client</groupId>
    <artifactId>cas-client-support-springboot</artifactId>
    <version>3.6.1</version>
</dependency>
```

#### ②	jar包方式

将目录中的jars/springboot中的jar包引入项目中（核心jar包为`cas-client-*`开头的三个jar包，其他的为该jar包依赖的包，根据自己项目是否已经引入确定是否加入)，完整的依赖如下,如果无法使用，请根据下述依赖去maven仓库中下载对应的包引入自己项目中:

![image-20191204160133494](C:\Users\Gahon\AppData\Roaming\Typora\typora-user-images\image-20191204160133494.png)

### 2. 自定义配置文件

将`cas`目录下的所有文件复制到项目中，并且修复相关错误。

### 3. 配置全局用户信息获取

请根据后端返回的属性修改`UserProfile.java`类为正确的内容，保证`buildFromAttributes()`方法不被修改。

配置好以后，便可以在其他需要获取当前登录的用户信息的地方调用 `CasUtils.getLoginUserInfo()` 便能够获取到用户信息。

### 4.  配置`application.properties`

在`application.properties`文件中根据自己项目修改下列配置

```properties
# cas配置
# 配置是否启用cas
cas-config.enable=true
# cas服务端前缀,不是登录地址，
cas.server-url-prefix=http://ip:port/cas
# cas的登录地址
cas.server-login-url=http://ip:port/cas/login
# 当前客户端的地址(如果与服务端配置相同的话会导致无法单点登出）
cas.client-host-url=http://ip:port/xxx
# 退出登录以后跳转回来的地址
cas-config.logout-default-url=http://ip:port/xxx
# 是否中央退出登录， 默认为true， 调用退出登录时，
# 先销毁本地session，然后在调用cas服务端进行中央退出登出
cas-config.central-logout=true
# 需要进行权限校验的地址？
#cas.validation-url-patterns=/*,
# 需要进行登录验证的uri, 逗号分隔， 编写规则见filter的url-patterns编写规则
cas.authentication-url-patterns=/*,
# 不进行cas验证的路径， 匹配规则见自定义排除路径匹配类，其中自带的匹配规则为Ant规则
cas-config.ignore-pattern=/ignore_path1, /ignore_path2
# 自定义排除路径匹配的类， 请根据自己的项目修改为正确的路径
cas-config.ignore-url-pattern-type=your.project.path.cas.CustomExcludePathMatchStrategy
```

### 5. 启用CAS

配置文件中将`cas-config.enable`设置为true 便可启用单点登录.

### 6. 单点登出配置

调用服务端进行登出地址为`cas.server-url-prefix + /logout?service=登出以后跳转回来的地址`

建议： 在调用之前先调用`session.invalidate()`注销本地session，然后在调用单点登出，否则可能会导致service端退出登录后回调时，client端还没来得及接收到退出登录请求，从而使得回调后依旧是登录状态。（详细请看 https://github.com/apereo/java-cas-client#recommend-logout-procedure）

**`CasLogoutController.java`中已经做好了上述的单点登出的controller了，默认登出地址为 `/cas/logout`,  可以根据自己需求进行修改。**

### 7. 配置不过滤某些地址

通过自定义`UrlPatternMatcherStrategy`和`pathPattern`来达到跳过验证的目的。

已经自带了一个路径排除策略（Ant规则，不懂请百度），可以只需要配置好`cas-config.ignore-pattern` 和 `cas-config.ignore-url-pattern-type`便能直接使用了，如果自带的不能够满足，请看末尾自定义教程。

### 8. 集成后测试

1. 访问一个被拦截的url，验证是否能跳转到登录页面，并且在登录后能够正确验证
2. 验证是否能够在登录以后，通过`CasUtils.getLoginUserInfo()`获取登录用户信息
3. 验证是否能够跳过不需要验证的URL（如果全部需要验证则可以忽略该测试)
4. 验证是否能够正确登出（表现为退出登录以后再次进入登录页面（如果回调页面需要登录的话）
5. 验证在服务端直接退出登录以后，客户端是否需要重新登录

### 9. 其他配置

详见 **Github** (https://github.com/apereo/java-cas-client#spring-boot-autoconfiguration)

### 10. FAQ

统一在文件末尾部分

## 二、 Spring MVC(web.xml)配置

### 1.  引入依赖

#### ①	maven方式

```xml
<dependency>
    <groupId>org.jasig.cas.client</groupId>
    <artifactId>cas-client-core</artifactId>
    <version>3.6.1</version>
</dependency>
```

#### ②	jar包方式

将目录中的jars/springmvc中的jar包引入项目中（核心jar包为`cas-client-*`开头的jar包，其他的为该jar包依赖的包，根据自己项目是否已经引入确定是否加入)，完整的依赖如下,如果无法使用，请根据下述依赖去maven仓库中下载对应的包引入自己项目中:

![image-20191204160618120](C:\Users\Gahon\AppData\Roaming\Typora\typora-user-images\image-20191204160618120.png)

###  2.  配置`web.xml`

在配置之前，需要先将`springmv/cas`下的所有文件复制到项目中，并解决相关包问题，然后在web.xml中配置如下

```xml
<!-- ====================	单点登录配置开始	==================== -->
<!--	配置单点登出	-->
<!--	定义单点登出Filter (该必须放在最前边)	-->
<filter>
    <filter-name>CAS Single Sign Out Filter</filter-name>
    <filter-class>org.jasig.cas.client.session.SingleSignOutFilter</filter-class>
    <init-param>
        <param-name>casServerUrlPrefix</param-name>
        <param-value>http://ip:port/cas</param-value> <!-- cas server前缀地址 -->
    </init-param>
</filter>
<!--	单点登出Listener	-->
<listener>
    <listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
</listener>

<!--	配置单点登录认证器	-->
<filter>
    <filter-name>CAS Authentication Filter</filter-name>
    <!--<filter-class>org.jasig.cas.client.authentication.Saml11AuthenticationFilter</filter-class>-->
    <filter-class>org.jasig.cas.client.authentication.AuthenticationFilter</filter-class>
    <init-param>
        <param-name>casServerLoginUrl</param-name>
        <param-value>http://ip:port/cas/login</param-value> <!-- cas server前缀地址 -->
    </init-param>
    <init-param>
        <param-name>serverName</param-name>
        <param-value>http://ip:port/xxx</param-value> <!-- cas client地址， #号后边的内容会丢失 -->
    </init-param>
    <init-param>
        <param-name>ignoreUrlPatternType</param-name>
        <!--    可填自定义路径匹配类的cas自带的四种匹配名称 -->
        <param-value>your.project.path.cas.CustomExcludePathMatchStrategy</param-value>
    </init-param>
    <init-param>
        <param-name>ignorePattern</param-name>
        <!--    不需要进行登录验证的路径, 示例采用的是antMatcher模式,根据自定义的编写   -->
        <param-value>/ignore_path1,/ignore_path2</param-value>
    </init-param>
</filter>

<filter>
    <filter-name>CAS Validation Filter</filter-name>
    <!--<filter-class>org.jasig.cas.client.validation.Saml11TicketValidationFilter</filter-class>-->
    <filter-class>org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter</filter-class>
    <init-param>
        <param-name>casServerUrlPrefix</param-name>
        <param-value>http://ip:port/cas</param-value>  <!-- cas server前缀地址 -->
    </init-param>
    <init-param>
        <param-name>serverName</param-name>
        <param-value>http://ip:port/xxx</param-value>    <!-- cas client前缀地址   #号后边的内容会丢失-->
    </init-param>
    <init-param>
        <param-name>redirectAfterValidation</param-name>
        <param-value>true</param-value>
    </init-param>
    <init-param>
        <param-name>useSession</param-name>
        <param-value>true</param-value>
    </init-param>
    <init-param>
        <param-name>authn_method</param-name>
        <param-value>mfa-duo</param-value>
    </init-param>
</filter>

<filter>
    <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
    <filter-class>org.jasig.cas.client.util.HttpServletRequestWrapperFilter</filter-class>
</filter>

<filter>
    <filter-name>CAS Assertion Thread Local Filter</filter-name>
    <filter-class>org.jasig.cas.client.util.AssertionThreadLocalFilter</filter-class>
</filter>
<filter>
    <filter-name>CAS Custom Context Thread Local Filter</filter-name>
    <!--    请更改为正确的路径    -->
    <filter-class>your.project.path.cas.ContextThreadLocalFilter</filter-class>
</filter>

<!--    配置退出登录过滤器 -->
<filter>
    <filter-name>CAS Logout Filter</filter-name>
    <!--    请更改为正确的路径    -->
    <filter-class>your.project.path.cas.CasLogoutFilter</filter-class>
    <init-param>
        <!--   退出登录以后跳转回来的地址   -->
        <param-name>logoutDefaultUrl</param-name>
        <param-value>http://cas.client.com:8080</param-value>
    </init-param>
    <init-param>
        <!--   cas 服务端地址   -->
        <param-name>serverUrlPrefix</param-name>
        <param-value>http://ip:port/cas</param-value>
    </init-param>
    <init-param>
        <!--    是否中央退出登录    -->
        <param-name>centralLogout</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>
<!--	配置过滤器拦截地址	-->
<!--	单点登出过滤器， 必须放在第一位	-->
<filter-mapping>
    <filter-name>CAS Single Sign Out Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter-mapping>
    <filter-name>CAS Validation Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter-mapping>
    <filter-name>CAS Authentication Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter-mapping>
    <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
<!-- 下边两个Filter顺序不能更改 -->
<filter-mapping>
    <filter-name>CAS Assertion Thread Local Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter-mapping>
    <filter-name>CAS Custom Context Thread Local Filter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter-mapping>
    <filter-name>CAS Logout Filter</filter-name>
    <!--    退出登录的路径     -->
    <url-pattern>/cas/logout</url-pattern>
</filter-mapping>
<!-- ====================	单点登录配置结束	==================== -->
```

### 3. 配置全局用户信息获取

#### ① 使用自定义类保存用户信息（更直观，容易理解）

在其他需要获取当前登录的用户信息的地方调用 `CasUtils.getLoginUserInfo()` 便能够获取到用户信息。

#### ② 使用原生获取信息

直接使用cas自带的方法 `AssertionHolder.getAssertion().getPrincipal()`方法便能获取登录Principal（注： `getAssertion()`方法获取到的对象可能为空，需要注意空指针异常。

### 4. 配置登出

登出地址格式为 `http(s)://cas退出登录地址?service=登出以后跳转回来的地址`

建议： 在调用之前先调用`session.invalidate()`注销本地session，然后在调用单点登出，否则可能会导致service端退出登录后回调时，client端还没来得及接收到退出登录请求，从而使得回调后依旧是登录状态。（详细请看 https://github.com/apereo/java-cas-client#recommend-logout-procedure）

**目前已经实现了退出登录的Filter，在web.xml中做好相应的配置就行。**

### 5. 配置不过滤某些地址

通过自定义`UrlPatternMatcherStrategy`和`pathPattern`来达到跳过验证的目的。

已经自带了一个路径排除策略（Ant规则，不懂请百度），可以只需要在 `CAS Authentication Filter` 中配置好`ignore-pattern` 和 `ignore-url-pattern-type`便能直接使用了，如果自带的不能够满足，请看末尾自定义教程。

### 6. 集成后测试

1. 访问一个被拦截的url，验证是否能跳转到登录页面，并且在登录后能够正确验证
2. 验证是否能够在登录以后，通过`CasUtils.getLoginUserInfo()`获取登录用户信息
3. 验证是否能够跳过不需要验证的URL（如果全部需要验证则可以忽略该测试)
4. 验证使用redirect方式退出登录是否能够正确登出（表现为再次进入登录页面（如果回调页面需要登录的话）
5. 验证在服务端直接退出登录以后，客户端是否需要重新登录

### 9. 其他配置

详见 **Github** (https://github.com/apereo/java-cas-client#client-configuration-using-webxml)

## 三、 配置对某些路径不进行验证

由于servlet filter不支持exclude path， 所以以上配置默认是对所有路径进行拦截的，如果要通过filter排除掉一部分路径将非常复杂，好在cas给我们提供了自定义的方式，我们可以通过继承`UrlPatternMatcherStrategy`类来自定义排除逻辑，或者使用cas自带的四种逻辑。 在这里我只讲怎么通过自定义的方式来排除部分路径，自带的四种逻辑使用详情请看（https://github.com/apereo/java-cas-client#ignore-patterns)

### ①	自定义排除类的示例

话不多说，直接看代码示例吧，该类也可以直接使用。

通过`setPattern()`将配置中的`ignorePattern`参数的内容给传进来，然后要通过`matches(url)`方法判断当前路径是不是不需要登录验证。

```java
package com.gahon.cas;

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
public class CustomExcludePathMatchStrategy implements UrlPatternMatcherStrategy {

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
            for (String excludedPath : patterns) {
                String uriPattern = excludedPath.trim();
                if (pathMatcher.match(uriPattern.trim(), path)) {
                    return true;
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
        this.patterns = pattern.trim().split(",");
    }
}
```

## 四、 其他配置

要想有更多的配置，请自行百度，或者参考官方github(https://github.com/apereo/java-cas-client)

## 五、 FAQ

### 1.  退出登录后跳转回来页面还是主页面

该问题可能有两个原因，一个是在退出登录时没有先销毁本地session，导致在跳转回来请求页面时，系统还没有收到cas server传回的注销session请求，从而导致判断为已登录状态，另一个原因是因为浏览器缓存导致的。

如果使用本文档附带的退出登录controller或logoutFilter，应该不会出现问题一，

原因二的解决方法可以通过设置一个响应头 `response.setHeader("Cache-Control", "no-cache");` 来禁用浏览器缓存，比如将该代码添加到`CORSFilter`中便可解决。