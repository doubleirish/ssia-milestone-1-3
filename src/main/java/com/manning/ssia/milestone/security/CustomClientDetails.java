package com.manning.ssia.milestone.security;

import com.manning.ssia.milestone.jpa.Client;
import com.manning.ssia.milestone.jpa.Grant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;
import java.util.stream.Collectors;


public class CustomClientDetails implements ClientDetails {

    private final Client client;

    public CustomClientDetails(Client client) {
        this.client =client;
    }

    @Override
    public String getClientId() {
        return client.getName();
    }

    @Override
    public Set<String> getResourceIds() {
        return null;
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return client.getSecret();
    }

    @Override
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return new HashSet<>(Arrays.asList(client.getScope() ));

    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return client.getGrants().stream()
                .map(Grant::getGrant)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return new HashSet<>(Collections.singletonList(client.getRedirectUri()));
    }
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
       return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"));


    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return 3600 * 4;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return 3600 * 4;
    }

    @Override
    public boolean isAutoApprove(String s) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }


    @Override
    public String toString() {
        return "CustomClientDetails{" +
                "clientId=" + getClientId() +
                "getAuthorizedGrantTypes=" + this.getAuthorizedGrantTypes()  +
                "authorities=" + this.getAuthorities()  +
                '}';
    }
}
