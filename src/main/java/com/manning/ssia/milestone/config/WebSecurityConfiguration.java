package com.manning.ssia.milestone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

//
// @Order(-5001)
@Configuration
@EnableWebSecurity(debug = false) // never run debug=true in production
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }







    @Bean
    public PasswordEncoder passwordEncoder() {

        String idForEncode = "bcrypt";
        Map encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());

        return new DelegatingPasswordEncoder(idForEncode, encoders);

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
                .authorizeRequests()
               // .mvcMatchers("/users").permitAll()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/clients/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
        ;
    }




}
