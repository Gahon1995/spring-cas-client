package com.gahon.springboot.cas.core.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 该类为自定义用户信息类，
 * <p>
 * 用户根据cas服务端传回来的用户信息进行自定义
 * <p>
 * 如果不知道具体有哪些属性，请打断点或者打印该类中的attributes属性即可
 * <p>
 * 只需要生成get方法和toString就行
 *
 * @author Gahon
 * @date 2019/11/5
 */
public class UserProfile {

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 密码，最长不超过20位
     */
    @JsonIgnore
    private String password;


    /**
     * 保存了从cas返回的所有属性
     */
    private Map<String, Object> attributes;

    /**
     * @param attributes 用户信息键值对，cas server 传回
     */
    private UserProfile(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.id = (String) attributes.get("id");
        this.userName = (String) attributes.get("username");
        this.password = (String) attributes.get("password");
//        请根据上述格式自定义该类，将返回属性列表中需要的信息保存在该类中，方便其他地方获取, 自定义完成以后便可以删除下边内容了
        System.out.println("当前返回属性: ");
        final List<String> strings = Arrays.asList("credentialType", "isFromNewLogin", "authenticationDate", "authenticationMethod", "successfulAuthenticationHandlers", "longTermAuthenticationRequestTokenUsed");
        attributes.forEach((s, o) -> {
            if (!strings.contains(s)) {
                System.out.println(s + " = " + o);
            }
        });
        throw new RuntimeException("未自定义用户信息类, 请根据上边的属性自定义用户信息表");
    }

    /**
     * 建立一个新的用户信息， 该方法不可更改
     *
     * @param attributes cas 传回的attributes
     * @return 根据属性创建好的用户对象
     */
    public static UserProfile buildFromAttributes(Map<String, Object> attributes) {
        return new UserProfile(attributes);
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password +
                '}';
    }
}
