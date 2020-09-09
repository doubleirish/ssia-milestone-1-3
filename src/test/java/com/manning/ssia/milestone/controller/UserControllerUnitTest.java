package com.manning.ssia.milestone.controller;

import com.manning.ssia.milestone.domain.UserDomain;
import com.manning.ssia.milestone.domain.UserDomainBuilder;
import com.manning.ssia.milestone.service.UserAlreadyExistsException;
import com.manning.ssia.milestone.service.UserNotFoundException;
import com.manning.ssia.milestone.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

//    @Test
//    @WithMockUser(value = "spring")
    public void whenUserDoesNotExistReturn404() throws Exception {


        int notFoundUserId = 999;
        when(userService.findByid(notFoundUserId) ).thenThrow(UserNotFoundException.class);

        given(passwordEncoder.encode("pass")).willReturn("encodedpass");
        mockMvc
                .perform(get("/users/"+notFoundUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        //        .andExpect(content().string("user exists"))
        ;

    }


}