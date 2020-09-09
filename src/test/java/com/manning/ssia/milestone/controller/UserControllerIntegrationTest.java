package com.manning.ssia.milestone.controller;

import com.manning.ssia.milestone.domain.UserDomain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void usersListAsUser() throws Exception {
        String jsonStr = this.restTemplate
                .withBasicAuth("john", "12345")
                .getForObject("http://localhost:" + port + "/users", String.class);
        assertThat(jsonStr).contains("john");
    }

    @Test
    public void userById() throws Exception {
        UserDomain userDomain = this.restTemplate
                .withBasicAuth("john", "12345")
                .getForObject("http://localhost:" + port + "/users/1", UserDomain.class);
        assertThat(userDomain.getId()).isEqualTo(1);
        assertThat(userDomain.getUsername()).isEqualTo("john");
    }


    @Test
    public void createUser() throws Exception {
        UserDomain userDomain = new UserDomain();
        userDomain.setUsername("newuser");
        userDomain.setPassword("newpass");
        userDomain.setAuthorities(Arrays.asList("user", "admin"));
        UserDomain returnUserDomain = this.restTemplate
                .withBasicAuth("john", "12345")
                .postForObject("http://localhost:" + port + "/users", userDomain, UserDomain.class);

        System.out.println(returnUserDomain);

        assertThat(returnUserDomain.getUsername()).isEqualTo(userDomain.getUsername());
    }

}