package com.sample.FinalProject.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.sample.FinalProject.model.User;
import com.sample.FinalProject.repo.UserRepo;

@Component("vendorSecurity")
public class VendorSecurity {
	@Autowired
    private UserRepo repo;

    public boolean isApproved(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> user = repo.findByEmail(email);
        if(user.isEmpty()) {
        	return false;
        }
        return user.get().isApproved();
    }
}
