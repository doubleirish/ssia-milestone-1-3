package com.manning.ssia.milestone.jpa;

import java.util.Arrays;
import java.util.List;

public final class UserBuilder {
    private int id;
    private String username;
    private String password;
    private List<Authority> authorities;

    private UserBuilder() {
    }

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
        return this;
    }



    public User build() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setAuthorities(authorities);
        return user;
    }
}
