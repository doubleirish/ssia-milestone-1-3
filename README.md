#### SSIA Live Project Milestone 1.3
Build an Oauth Server part 3  - replace in-memory userDetailsService with a custom UserDetailsService that is DB backed</description>
 
#####
Milestone 1.2 is probably going to take most folks close to 10 hours to complete
if you find in practice that students are taking longer than 10 hours, it is probably possible to break this project in further sub projects
one to build/test the data tier and one to build/test the controller/service tiers. 

Milestone 1.3 should be about 4 hours unless they get into difficulties. 
FYI, I refactored my Impl between milestone 1.2 and milestone 1.3 to add a service layer

because of a variety of skill levels between students,
 some will need the full 8-10 hours and some might finish earlier.
 
One Option to keep smart/lucky students from getting bored are Bonus tasks.

Since I had some extra time available for milestone 1.3, I worked on some optional tasks :-
- add JSR 303 validation to your endpoints for adding a user or a client
- add a Unit Test and postman to test your JSR 303 valiation  
- add a @JsonTest to verify JSON serialization and deSerialization 
- Add a Github Action to build your milestone in the cloud


 
##### Suggested  recommended reading list 
 
1. SSIA Chapter 3  -UserDetailsService
1. Spring in Action 
1. Cloud native Spring in Action, section 3.4 on Testing your web, service and data tiers
  
  
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


## Implement a strong Passwword Decoder 

##### replace your noop password encoder with a delegating password encoder
the delegating password encoder is great as it allows you to have multiple password decoders active at once.
so you could piecemail update the hash to a more secure one as users passwords expire rather than forcing everone to update at once.
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
 
 
 # Bonus
 
 ## Add JSR 303 Validation to 
 enabling a validator will allow us to verify that not only are sudmitted users and clients fields present but they are symatically correct too
 ##### add validation dependency to pom.xml
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
``` 
##### add jsr303 validation annotations to your user and client domain objects

```

@Data
public class ClientDto {
    private int id;
    @NotBlank(message = "the client name must be defined")
    private String name;

```
##### add the @Valid annotation to the controller POST methods
```
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ClientDto createClient(@Valid @RequestBody ClientDto clientDomain) {
        Client client = convertToEntity(clientDomain);
        client = clientRepository.save(client);
        log.info("created client {}",client);
        return convertToDto(client);
    }

```

##### send a postman request with an invalid user or client
e.g sending a blank client name ...
```
{
        
        "name": "",
        "secret": "newsecret",
        "scope": "read",
        "redirectUri": "http://localhost:8181/",
        "grants": [
            "authorization_code",
            "password",
            "client_credentials",
            "refresh_token"
        ]
    }
``` 
should return a standard 400 BAD_REQUEST message
```{
       "timestamp": "2020-09-08T04:06:34.745+00:00",
       "status": 400,
       "error": "Bad Request",
       "message": "",
       "path": "/clients"
   }
```
To get a better error message add a RestControllerAdvice class
```
package com.manning.ssia.milestone.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CentralControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleValiationExceptions(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error ->{
            String fieldName= ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName,errorMessage);
        });
        return errors;

    }
}

```
##### add a constraint validation test 
```

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserValidationTests {
    private static Validator validator;

    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsCorrectThenValidationSucceeds() {

        UserDomain user = new UserDomain(1, "user", "pass", Arrays.asList("admin", "accounting"));
        Set<ConstraintViolation<UserDomain>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    public void whenUsernameIsMissingThenValidationFails() {
        UserDomain user = new UserDomain(1, "", "pass", Arrays.asList("admin", "accounting"));
        Set<ConstraintViolation<UserDomain>> violations = validator.validate(user);
        assertThat(violations).hasSize(1);
    }
}
```

### Add a @JsonTest

```

```