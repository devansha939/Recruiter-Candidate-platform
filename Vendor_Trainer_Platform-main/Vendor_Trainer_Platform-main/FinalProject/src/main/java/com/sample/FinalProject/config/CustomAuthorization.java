package com.sample.FinalProject.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component("customAuth")
public class CustomAuthorization {

    public boolean isApproved(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUser) {
            return customUser.getUser().isApproved();
        }
        return false;
    }
}
