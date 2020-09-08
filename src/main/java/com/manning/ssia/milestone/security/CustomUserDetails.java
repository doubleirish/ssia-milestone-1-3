package com.manning.ssia.milestone.security;

import com.manning.ssia.milestone.domain.UserDomain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails  implements UserDetails {
        private UserDomain userDomain;

        public CustomUserDetails(UserDomain userDomain) {
            this.userDomain = userDomain;
        }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDomain.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userDomain.getPassword();
    }

    @Override
    public String getUsername() {
        return userDomain.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "username=" + userDomain.getUsername() +
                "password=" + userDomain.getPassword() +
                ", authorities=" + this.getAuthorities()  +
                '}';
    }
}
