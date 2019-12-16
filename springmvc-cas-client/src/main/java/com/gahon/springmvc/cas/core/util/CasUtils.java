package com.gahon.springmvc.cas.core.util;

import com.gahon.springmvc.cas.core.profile.UserProfile;
import org.jasig.cas.client.authentication.AttributePrincipal;

/**
 * @author Gahon
 * @date 2019/11/5
 */
public class CasUtils {

    private static final ThreadLocal<UserProfile> THREAD_LOCAL = new ThreadLocal<>();

    public static void setProfile(final AttributePrincipal principal) {
        UserProfile userProfile = UserProfile.buildFromAttributes(principal.getAttributes());
        THREAD_LOCAL.set(userProfile);
    }

    public static UserProfile getProfile() {
        return THREAD_LOCAL.get();
    }

    public static UserProfile getLoginUserInfo() {
        return THREAD_LOCAL.get();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
