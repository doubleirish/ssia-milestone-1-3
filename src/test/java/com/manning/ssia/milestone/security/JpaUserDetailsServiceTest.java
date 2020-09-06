package com.manning.ssia.milestone.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class JpaUserDetailsServiceTest {

    @Resource(name = "jpaUserDetailsService")
    private JpaUserDetailsService userDetailsService;

    @Test
    void loadUserByUsernameJohnIsFound() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("john");
        assertThat(userDetails.getUsername()).isEqualTo("john");
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsernamenot_found() {
         assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("baduser");
        });
    }
}