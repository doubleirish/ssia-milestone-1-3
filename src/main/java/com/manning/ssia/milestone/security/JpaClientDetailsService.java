package com.manning.ssia.milestone.security;

import com.manning.ssia.milestone.jpa.Client;
import com.manning.ssia.milestone.jpa.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class JpaClientDetailsService implements ClientDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientName) throws ClientRegistrationException {
        log.info("looking up client {}",clientName);
         Client client = clientRepository.findByName(clientName)
                .orElseThrow(() -> new ClientRegistrationException("client not found :"+clientName)  );

        CustomClientDetails clientDetails= new CustomClientDetails(client);

        log.info("found clientDetails {}",clientDetails);
        return clientDetails;
    }
}
