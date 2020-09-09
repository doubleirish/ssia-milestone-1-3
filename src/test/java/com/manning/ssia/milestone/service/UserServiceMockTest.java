package com.manning.ssia.milestone.service;

import com.manning.ssia.milestone.domain.UserDomain;
import com.manning.ssia.milestone.domain.UserDomainBuilder;
import com.manning.ssia.milestone.jpa.User;
import com.manning.ssia.milestone.jpa.UserBuilder;
import com.manning.ssia.milestone.jpa.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


public class UserServiceMockTest {

    @Test
    public void whenFindUserNotExistsThrowsNotFoundException ()   {
        UserRepository userRepository = mock(UserRepository.class);

        given(userRepository.findByUsername("not-exists")).willReturn( Optional.empty() );

        UserService userService = new UserService(userRepository);

        assertThrows(UserNotFoundException.class, () -> {
            userService.findByUsername("not-exists");
        });
    }


    @Test
    public void whenAddUserAlreadyExistsThrowException()   {


        UserRepository userRepository = mock(UserRepository.class);
        given(userRepository.existsByUsername("exists")).willReturn(true);
        UserService userService = new UserService(userRepository);

        UserDomain userDomain = UserDomainBuilder.aUserDomain()
                .withId(1)
                .withUsername("exists")
                .withPassword("pass")
                .build();
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.addUser(userDomain);
        });
    }


}