package com.manning.ssia.milestone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manning.ssia.milestone.domain.UserDomain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTests {

    @Autowired
    private JacksonTester<UserDomain> json;


    @BeforeEach
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void testSerialize() throws Exception {
        UserDomain user = new UserDomain(1,"john","pass", "ROLE_USER");
        assertThat(json.write(user))
                .extractingJsonPathStringValue("@.username")
                .isEqualTo(user.getUsername());
    }

    @Test
    public void testDeserialize() throws Exception {
        UserDomain user = new UserDomain(1,"john","12345", "ROLE_USER");
        String jsonString = "{\n" +
                "    \"id\": 1,\n" +
                "    \"username\": \"john\",\n" +
                "    \"password\": \"12345\",\n" +
                "    \"authorities\": [\n" +
                "        \"ROLE_USER\"\n" +
                "    ]\n" +
                "}";
        assertThat(json.parse(jsonString))
                .isEqualToComparingFieldByField(user);
    }

}
