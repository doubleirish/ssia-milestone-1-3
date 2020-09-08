package com.manning.ssia.milestone.security;

import com.manning.ssia.milestone.controller.UserDomain;
import com.manning.ssia.milestone.jpa.User;
import com.manning.ssia.milestone.jpa.UserRepository;
import com.manning.ssia.milestone.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("looking up user {}", username);
        UserDomain userDomain = userService.findByUsername(username);

        CustomUserDetails userDetails = new CustomUserDetails(userDomain);
        log.info("found userdetails {}", userDetails);
        return userDetails;
    }
}
