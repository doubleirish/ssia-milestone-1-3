package com.manning.ssia.milestone.controller;

import com.manning.ssia.milestone.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users", produces = "application/json")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Iterable<UserDomain> listUsers() {
        return userService.list();
    }


    @GetMapping("{id}")
    public ResponseEntity<UserDomain> userById(@PathVariable("id") Integer id) {
        UserDomain userDomain = userService.findByid(id);
        return new ResponseEntity<>(userDomain, HttpStatus.OK);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserDomain createUser(@Valid @RequestBody UserDomain userDomain) {
        userDomain.setPassword( passwordEncoder.encode(userDomain.getPassword()));
        return userService.addUser(userDomain);
    }

}

