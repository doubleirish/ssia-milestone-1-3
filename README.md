#### SSIA Live Project Milestone 1.3
Build an Oauth Server part 3  - replace in-memory userDetailsService with a custom UserDetailsService that is DB backed</description>
 
##### Suggested  recommended reading list 
 
1. SSIA Chapter 3  
  
  
## Create a Custom UserDetailsService  
 
###  first create a Custom UserDetails  class that implements the Spring Security UserDetails interface
You'll need to create a Custom User class that implements the following interface
```
package org.springframework.security.core.userdetails;

public interface UserDetails extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();
    String getPassword();
    String getUsername();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();
}
```
I can use the JPA user to help construct the SpringSecurity  Custom UserDetails instance
```
import com.manning.ssia.milestone.jpa.Authority;
import com.manning.ssia.milestone.jpa.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetails  implements UserDetails {
        private User user;

        public CustomUserDetails(User user) {
            this.user = user;
        }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities().stream()
                .map(Authority::getAuthority)
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "username=" + user.getUsername() +
                "password=" + user.getPassword() +
                ", authorities=" + this.getAuthorities()  +
                '}';
    }
}
```


##### Create A custom userDetailsService
this user will connect to your User Jpa repositry to find a JPA user and convert it  into UserDetails
object that is returned
```

import com.manning.ssia.milestone.jpa.User;
import com.manning.ssia.milestone.jpa.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("looking up user {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("did not find user {}", username);
            throw new UsernameNotFoundException(username);
        }
        com.manning.ssia.milestone.security.CustomUserDetails userDetails = new com.manning.ssia.milestone.security.CustomUserDetails(user);
        log.info("found userdetails {}", userDetails);
        return userDetails;
    }
}
```
#####  add a unit test for you new custom user details service 
```

@SpringBootTest
class JpaUserDetailsServiceTest {

    @Resource(name = "jpaUserDetailsService")
    private JpaUserDetailsService userDetailsService;

    @Test
    void loadUserByUsernameJohnIsFound() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("john");
        assertThat(userDetails.getUsername()).isEqualTo("john");
        assertThat(userDetails.isEnabled()).isTrue();
    }

  @Test
    void loadUserByUsernamenot_found() {
         assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("baduser");
        });
    }
}
```

  
## Create a Custom JPA based ClientDetailsService  


##### create a Custom ClientDetails class 
again I can use the JPA Client to help populate the Spring Security ClientDetails
```

import com.manning.ssia.milestone.jpa.Client;
import com.manning.ssia.milestone.jpa.Grant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
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
        return 300;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return 300;
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
```

##### implement a custom JpaClientDetailsService class that implements the ClientDetailsService interface
this   will connect to your Client Jpa repository to find a JPA Client and convert it  into ClientDetails
object that is returned
```

import com.manning.ssia.milestone.jpa.Client;
import com.manning.ssia.milestone.jpa.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JpaClientDetailsService implements ClientDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientName) throws ClientRegistrationException {
        log.info("looking up client {}",clientName);
        Client client = clientRepository.findByName(clientName);
        if (client == null) {
            log.error ("did not find client {}",clientName);
            throw new ClientRegistrationException(clientName);
        }
        CustomClientDetails clientDetails= new CustomClientDetails(client);

        log.info("found clientDetails {}",clientDetails);
        return clientDetails;
    }
}
```
 
##### Create a unit test for your custom clientDetailsService

```import org.junit.jupiter.api.Test;
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
```

##### replace the in-memory clientDetailsService with your JPA impl
```

@Configuration
@EnableAuthorizationServer
public class OAuthConfig extends AuthorizationServerConfigurerAdapter {

   ...

    @Resource(name = "jpaClientDetailsService")
    private ClientDetailsService clientDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

//    @Override
//    public void configure(  ClientDetailsServiceConfigurer clients)
//            throws Exception {
//        clients.inMemory()
//                .withClient("client")
//                .secret("secret")
//                .authorizedGrantTypes("password","authorization_code","client_credentials","refresh_token")
//                .scopes("read");
//    }
```


##### replace your noop password encoder with a delegating password encoder

```

// @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

   @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
```
##### temporarily add a {noop} prefix to the place where your store passwords ands secrets
```
insert into USER (ID, USERNAME, PASSWORD) values (1, 'john' ,'{noop}12345); 

insert into CLIENT (ID, NAME, SECRET, REDIRECT_URI, SCOPE)
values (1, 'client','{noop}secret' ,'http://localhost:8181/', 'read');

```

rerun all your unit and postman tests, everything should still be usccessful

##### create a simple utility to convert a plain text password/secret into a bcrypt hash
```

public class PasswordEncoderTest {

    @Test
    public void becryptPassEncode() throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("secret= {bcrypt}" +encoder.encode("secret"));
        System.out.println("12345= {bcrypt}" +encoder.encode("12345"));

    }
}
```
##### replace the {noop} passwords with their {bcrypt} equivalent and rerun your tests
```
insert into USER (ID, USERNAME, PASSWORD) 
    values (1, 'john' ,'{bcrypt}$2a$10$iMMb7iGNjDAlqkwlR4TJHuhwtMGq.sMGL5v3TEiCt53vIiGke0cpa');

insert into CLIENT (ID, NAME, SECRET, REDIRECT_URI, SCOPE)
    values (1, 'client','{bcrypt}$2a$10$CVLUeCYqZQpLRm0PpaXXTuvskBujQelGhmxoCXXU0RylBrTQOiqQW' ,'http://localhost:8181/', 'read');
```
you can experiment with changing a character in the hash, your unit and postman tests should fail
 
 ##### rerun your oauth password grant  postman  test from milestone1
 if you've kept the same passwords between milestones your token grant request should still work
 except now it's no longer using a in-memory user/pass client/secret setup but pull those credentials and hashs from the database 