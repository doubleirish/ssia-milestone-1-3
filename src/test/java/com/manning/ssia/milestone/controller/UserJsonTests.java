package com.manning.ssia.milestone.controller;

import com.manning.ssia.milestone.domain.UserDomain;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTests {

    @Autowired
    private JacksonTester<UserDomain> json;

    @Test
    public void testSerialize() throws Exception {
        UserDomain user = new UserDomain(1,"john","pass", "accounting");
        assertThat(json.write(user))
                .extractingJsonPathStringValue("@.username")
                .isEqualTo(user.getUsername());
    }


}
