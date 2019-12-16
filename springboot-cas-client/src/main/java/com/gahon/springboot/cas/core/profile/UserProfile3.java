package com.gahon.springboot.cas.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * 该类为自定义用户信息类，
 * <p>
 * 用户根据cas服务端传回来的用户信息进行自定义
 * <p>
 * 如果不知道具体有哪些属性，请打断点或者打印该类中的attributes属性即可
 *
 * @author Gahon
 * @date 2019/11/5
 */
public class UserProfile3 {

    /**
     * 用户id
     */
    private String id;

    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 登录名，通常和用户名一致
     */
    private String loginName;

    /**
     * 密码，最长不超过20位
     */
    @JsonIgnore
    private String password;

    /**
     * 性别
     */
    private String sex;

    /**
     * 生日 yyyy-MM-dd
     */
    private String birthday;

    /**
     * 学历
     */
    private String education;

    /**
     * 组织结构id
     */
    private String organizationId;

    /**
     * 保存了从cas返回的所有属性
     */
    private Map<String, Object> attributes;

    /**
     * @param attributes 用户信息键值对，cas server 传回
     */
    private UserProfile3(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.id = (String) attributes.get("id");
        this.userName = (String) attributes.get("username");
        this.loginName = (String) attributes.get("login_name");
        this.password = (String) attributes.get("password");
        this.sex = (String) attributes.get("sex");
        this.birthday = (String) attributes.get("birthday");
        this.education = (String) attributes.get("education");
        this.organizationId = (String) attributes.get("organization_id");
    }

    /**
     * 建立一个新的用户信息， 该方法不可更改
     *
     * @param attributes cas 传回的attributes
     * @return 根据属性创建好的用户对象
     */
    public static UserProfile3 buildFromAttributes(Map<String, Object> attributes) {
        return new UserProfile3(attributes);
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }

    public String getSex() {
        return sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEducation() {
        return education;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String name) {
        return attributes == null ? null : attributes.get(name);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", education='" + education + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
