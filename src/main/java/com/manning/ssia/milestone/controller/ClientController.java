package com.manning.ssia.milestone.controller;


import com.manning.ssia.milestone.domain.ClientDomain;
import com.manning.ssia.milestone.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/clients", produces = "application/json")
@CrossOrigin(origins = "*")
public class ClientController {
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientController(ClientService clientService, PasswordEncoder passwordEncoder) {
        this.clientService = clientService;
        this.passwordEncoder =  passwordEncoder;
    }

    @GetMapping
    public Iterable<ClientDomain> list() {
        return clientService.list();
    }


    @GetMapping("/{id}")
    public ResponseEntity<ClientDomain> byId(@PathVariable("id") Integer id) {
         ClientDomain clientDomain = clientService.findById(id);
        return new ResponseEntity<>(clientDomain, HttpStatus.OK);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ClientDomain createClient(@Valid @RequestBody ClientDomain clientDomain) {
        clientDomain.setSecret( this.passwordEncoder.encode(clientDomain.getSecret()));
        return clientService.addClient(clientDomain);
    }
}

