package com.manning.ssia.milestone.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class PasswordEncoderTest {

    @Test
    public void becryptPassEncode() throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("secret= {bcrypt}" +encoder.encode("secret"));
        System.out.println("12345= {bcrypt}" +encoder.encode("12345"));

    }
}