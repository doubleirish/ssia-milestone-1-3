package com.manning.ssia.milestone.domain;

import java.util.Arrays;
import java.util.List;

public final class UserDomainBuilder {
    private int id;
    private String username;
    private String password;
    private List<String> authorities;

    private UserDomainBuilder() {
    }

    public static UserDomainBuilder aUserDomain() {
        return new UserDomainBuilder();
    }

    public UserDomainBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public UserDomainBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserDomainBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserDomainBuilder withAuthority( String authority) {
        this.authorities = Arrays.asList(authority);
        return this;
    }

    public UserDomainBuilder withAuthorities(List<String> authorities) {
        this.authorities = authorities;
        return this;
    }

    public UserDomain build() {
        UserDomain userDomain = new UserDomain();
        userDomain.setId(id);
        userDomain.setUsername(username);
        userDomain.setPassword(password);
        userDomain.setAuthorities(authorities);
        return userDomain;
    }
}
