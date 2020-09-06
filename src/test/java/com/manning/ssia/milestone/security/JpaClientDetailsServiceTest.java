package com.manning.ssia.milestone.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import javax.annotation.Resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class JpaClientDetailsServiceTest {

    @Resource(name = "jpaClientDetailsService")
    private JpaClientDetailsService clientDetailsService;

    @Test
    void loadClientByClientId_success() {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId("client");
        assertThat(clientDetails.getClientId()).isEqualTo("client");
        assertThat(clientDetails.isSecretRequired()).isTrue();
    }


    @Test
    void loadClientByClientId_not_found() {
        Exception exception = assertThrows(ClientRegistrationException.class, () -> {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId("badclient");
        });
    }
}