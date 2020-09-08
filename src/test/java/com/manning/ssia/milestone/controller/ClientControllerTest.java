package com.manning.ssia.milestone.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClientControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void clients() throws Exception {
        String jsonStr =  this.restTemplate.getForObject("http://localhost:" + port + "/clients",  String.class);
        assertThat(jsonStr).contains("client");
    }

    @Test
    public void clientById() throws Exception {
        ClientDomain clientDomain =  this.restTemplate.getForObject("http://localhost:" + port + "/clients/1",  ClientDomain.class);

        assertThat(clientDomain.getName()).isEqualTo("client");
    }


    @Test
    public void createClient() throws Exception {
        ClientDomain clientDomain = new ClientDomain();
        clientDomain.setName("newclient");
        clientDomain.setSecret("newpass");
        clientDomain.setScope("read");
        clientDomain.setGrants(Arrays.asList("user","admin","read"));
        clientDomain.setRedirectUri("http://localhost:8080/authorized");
        ClientDomain returnClientDomain =  this.restTemplate.postForObject("http://localhost:" + port + "/clients", clientDomain, ClientDomain.class);

        System.out.println(returnClientDomain);

        assertThat(returnClientDomain.getName()).isEqualTo(clientDomain.getName());
    }

}