package com.gahon.springboot.cas.controller;

import com.gahon.springboot.cas.core.util.CasUtils;
import com.gahon.springboot.cas.core.profile.UserProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gahon
 * @date 2019/11/5
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public String index(){
        final UserProfile userProfile = CasUtils.getLoginUserInfo();
        if (userProfile != null) {
            return "userProfile = " + userProfile;
        }
        return "用户未登录";
    }

    @GetMapping("/info")
    public String info(){
        final UserProfile userProfile = CasUtils.getLoginUserInfo();
        if (userProfile != null) {
            return "userProfile = " + userProfile;
        }
        return "用户信息为空";
    }
    @GetMapping("/ignore")
    public String test1(){
        return "ignore";
    }
}
