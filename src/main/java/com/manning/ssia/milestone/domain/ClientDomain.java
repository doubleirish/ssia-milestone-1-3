package com.manning.ssia.milestone.domain;

import com.manning.ssia.milestone.jpa.Client;
import com.manning.ssia.milestone.jpa.Grant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ClientDomain {
    private int id;
    @NotBlank(message = "the client name must be defined")
    private String name;
    @NotBlank(message = "the client secret must be defined")
    private String secret;
    @NotBlank(message = "the client scope must be defined")
    private String scope;
    private String redirectUri;

    @Size(min = 1)
    private List<String> grants;

    public ClientDomain() {
    }

    public ClientDomain(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.secret = client.getSecret();
        this.scope = client.getScope();
        this.redirectUri = client.getRedirectUri();
        this.grants = client.getGrants()
                .stream()
                .map(Grant::getGrant)
                .collect(Collectors.toList());
    }
}
