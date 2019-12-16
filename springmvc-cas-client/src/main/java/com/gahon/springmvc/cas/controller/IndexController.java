package com.gahon.springmvc.cas.controller;

import com.gahon.springmvc.cas.core.CasUtils;
import com.gahon.springmvc.cas.core.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Gahon
 * @date 2019/12/3
 */
@RestController
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/test")
    public String test(HttpServletRequest request){
//        final String remoteUser = request.getRemoteUser();
//        log.info(remoteUser);
//        final Principal userPrincipal = request.getUserPrincipal();
//        System.out.println(userPrincipal.toString());
//        System.out.println(AssertionHolder.getAssertion().getPrincipal().getAttributes().toString());
//        log.info("test");
//        final UserProfile loginUserInfo = CasUtils.getLoginUserInfo();
//        if (loginUserInfo != null) {
//            log.info(loginUserInfo.toString());
//            return "test: loginUser = " + loginUserInfo.toString();
//        }
        final UserProfile loginUserInfo = CasUtils.getLoginUserInfo();
        return "loginUser = " + loginUserInfo.toString();
    }

    @GetMapping("/ignore")
    public String ignore(){
        return "ignore";
    }

}
