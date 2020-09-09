package com.manning.ssia.milestone.service;

import com.manning.ssia.milestone.domain.UserDomain;
import com.manning.ssia.milestone.jpa.Authority;
import com.manning.ssia.milestone.jpa.User;
import com.manning.ssia.milestone.jpa.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Collection<UserDomain> list() {
        return userRepository.findAll(Sort.by("username").ascending())
                .stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());

    }


    public UserDomain findByid(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found by id" + id));
        return convertToDomain(user);
    }


    public UserDomain findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("user not found by name" + username));
        return convertToDomain(user);
    }

    @Transactional
    public UserDomain addUser(UserDomain userDomain) {
        if (userRepository.existsByUsername(userDomain.getUsername())) {
            throw new UserAlreadyExistsException("User already exists" + userDomain.getUsername());
        }
        User user = convertToEntity(userDomain);
        User savedUser = userRepository.save(user);
        return convertToDomain(savedUser);
    }

    private User convertToEntity(UserDomain userDomain) {
        User user = new User();
        user.setUsername(userDomain.getUsername());
        user.setPassword(userDomain.getPassword());
        List<String> auths = userDomain.getAuthorities();
        if (auths != null && auths.size() > 0) {
            List<Authority> authorities = auths
                    .stream()
                    .map(a -> new Authority(a, user))
                    .collect(Collectors.toList());

            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        return user;
    }

    private UserDomain convertToDomain(User user) {
        return new UserDomain(user);
    }


}
