package com.manning.ssia.milestone.service;

import com.manning.ssia.milestone.controller.ClientDomain;
import com.manning.ssia.milestone.controller.UserDomain;
import com.manning.ssia.milestone.jpa.Client;
import com.manning.ssia.milestone.jpa.ClientRepository;
import com.manning.ssia.milestone.jpa.Grant;
import com.manning.ssia.milestone.jpa.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Collection<ClientDomain> list() {
        return clientRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());

    }


    public ClientDomain findById(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("client not found " + id));
        return convertToDomain(client);
    }


    public ClientDomain findByName(String  name) {
        Client client  = clientRepository.findByName( name)
                .orElseThrow(() -> new ClientNotFoundException("client not found by name" + name));
        return convertToDomain(client);
    }
    @Transactional
    public ClientDomain addClient(ClientDomain clientDomain) {
        if (clientRepository.existsByName(clientDomain.getName())) {
            throw new UserAlreadyExistsException("User already exists" + clientDomain.getName());
        }
        Client client = convertToEntity(clientDomain);
        Client  savedClient = clientRepository.save(client);
        return convertToDomain(savedClient);
    }


    private Client convertToEntity(ClientDomain clientDomain) {
        Client client = new Client();
        client.setName(clientDomain.getName());
        client.setScope(clientDomain.getScope());
        client.setRedirectUri(clientDomain.getRedirectUri());
        client.setSecret(clientDomain.getSecret());

        List<Grant> grants = clientDomain.getGrants()
                .stream()
                .map(a -> new Grant(a, client))
                .collect(Collectors.toList());

        client.setGrants(grants);
        return client;
    }

    private ClientDomain convertToDomain(Client client) {
        return new ClientDomain(client);
    }


}
