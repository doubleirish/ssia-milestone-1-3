#### SSIA Live Project Milestone 1.3
Build an Oauth Server part 3  - replace in-memory userDetailsService with a custom UserDetailsService that is DB backed</description>
 
##### Suggested  recommended reading list 
 
1. SSIA Chapter 3  
  
  
## create a custom UserDetailsService  
 
###  create a CustomUserDetails  class that implements UserDetails 
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
##### Create a custom ClientDetailsService
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

##### create a User JPA Repository  interface
```
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository   extends JpaRepository<User, Integer> {
  User findByUsername(String name);
 }
```


##### create a JPA test class to test your User repository 

```
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate"
})
public class UserRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    void findByUsername() {
        User user = userRepository.findByUsername("john");
        assertThat(user.getUsername()).isEqualTo("john");
    }
}
```
both tests should succeed 
##### Create an Authority JPA Entity for the AUTHORITY Table 
- The Authority entity has many-to-one relationship with the User entity 
- and the authority's USER_ID column is the foreign key we use to  point back to the parent table
- it's a good idea to override the lombok toString() to prevent circular references when printing
```
@Data
@Entity
public class Authority {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String authority;

    
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user ;

    public Authority() {
    }

    public Authority(String authority, User user) {
        this.authority=authority;
        this.user=user;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", authority='" + authority + '\'' +
                '}';
    }
}
```

##### update the User Entity to include a collection of the authories it owns 
- The mappedBy value of "user" defines the field in the Authority class which is used to map back to the User Class
- you may also update the toString() to include the authorities field
```
  @Entity
  public class User {  

   ...   
  
      @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
      private List<com.manning.ssia.milestone.jpa.Authority> authorities;

```
##### Update your UserRepositoryTesT   class to verify that the user "john" has an Authority populated
```
 @Test
    void johnUserhasAtLeastOneAuthority() {
        User user = userRepository.findByUsername("john");
        assertThat(user.getAuthorities()).isNotEmpty();
    }
```
##### create a UserDetailsService (replaces in-memory equivalent)
- re-pruprosing the JPA User entity to implement the UserDetails interface is usually more trouble than it's ever worth
- instead create your own custom class inplementing the UserDetails interface which can work with our JPA User entity
```
public class CustomUserDetails  implements UserDetails {
        private User user;  //our JPA entity

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
                "authorities=" + this.getAuthorities()  +
                '}';
    }
}
```
##### create a UserDetailsService (replaces in-memory equivalent)
create your own JPA backed implementation of the UserDetailsService
```
@Slf4j
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("looking up user {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {  //TODO use Optional ?
            log.error("did not find user {}", username);
            throw new UsernameNotFoundException(username);
        }
        com.manning.ssia.milestone.security.CustomUserDetails userDetails = new com.manning.ssia.milestone.security.CustomUserDetails(user);
        log.info("found userdetails {}", userDetails);
        return userDetails;
    }
}
```

##### Add some JPA properties to application.properties
- JPA can also create the DB schema at start up. 
- you'll want to disable that as it will conflict with your schema.sql
```
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=none
```

#### Creatings a /users REST endpoint

##### Create a UserDto object that the controller will return 
I find it's best not to return JPA entity objects ina REST Controller but instead map them to a simple DTO
```
@Data
@NoArgsConstructor
public class UserDto {
    private int id;
    private String username;
    private String password; // this can be removed after debugging
    private List<String> authorities;

    UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities()
                .stream()
                .map(Authority::getAuthority)
                .collect(Collectors.toList());
    }
}
```

##### create a REST Contoller for the /users endpoint
 UserController
```

@Slf4j
@RestController
@RequestMapping(path = "/users", produces = "application/json")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
          this.passwordEncoder =  passwordEncoder;
    }

    @GetMapping
    public Iterable<UserDto> listUsers() {

        log.debug("user repo is size " + userRepository.count());
        //userRepo.findAll().forEach(t->log.debug("found user "+t));

        PageRequest page = PageRequest.of(
                0, 12, Sort.by("username").ascending());

        return userRepository
                .findAll(page)
                .getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private UserDto convertToDto(User user) {
        return new UserDto(user);
    }
}
``` 
After restarting the app you should be able to connect to http://localhost:8080/users/ and see all the users
```
[{"id":2,"username":"admin","password":"secret2","authorities":["ROLE_ADMIN"]},{"id":1,"username":"john","password":"12345","authorities":["ROLE_USER"]}]
```

##### Add a Web Test for the User Controller GET /users endpoint
```
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void usersListAsUser() throws Exception {
        String jsonStr = this.restTemplate
                .withBasicAuth("john", "12345")
                .getForObject("http://localhost:" + port + "/users", String.class);
        assertThat(jsonStr).contains("john");
    }
```

##### add a new POST /users endpoint to allow new users be added 
```

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = convertToEntity(userDto);
        user = userRepository.save(user);
        return convertToDto(user);
    }


    private UserDto convertToDto(User user) {
        return new UserDto(user);
    }

    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(this.passwordEncoder.encode(userDto.getPassword())); // TODO hash encode this later

        List<Authority> authorities = userDto.getAuthorities()
                .stream()
                .map(a -> new Authority(a,user))
                .collect(Collectors.toList());

        user.setAuthorities(authorities);
        userRepository.save(user);
        return user;
    }
```
##### add new tests for creating users in the controller
```
 @Test
    public void createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("newuser");
        userDto.setPassword("newpass");
        userDto.setAuthorities(Arrays.asList("user", "admin"));
        UserDto returnUserDto = this.restTemplate
                .withBasicAuth("john", "12345")
                .postForObject("http://localhost:" + port + "/users", userDto, UserDto.class);

        System.out.println(returnUserDto);

        assertThat(returnUserDto.getUsername()).isEqualTo(userDto.getUsername());
    }
```

## Setup a DB Backed ClientDetailsService 
##### Define CLIENT and GRANT tables
- Clients represent applications which may want to connect to your service.   
- We will define SQL tables to store the Client credentials and the zero or more Grants they may have
- update the src/main/resources/schema.sql  to include the following DDL
```
drop table if exists GRANT;
drop table if exists CLIENT;

create table if not exists CLIENT
(
    ID INT auto_increment   primary key,
    NAME  VARCHAR(255) not null,
    SCOPE VARCHAR(10) not null default 'user',
    SECRET VARCHAR(255) not null,
    REDIRECT_URI VARCHAR(255) not null
);


create table if not exists GRANT
(
    ID INT auto_increment   primary key,
    GRANT VARCHAR(50) not null,
    CLIENT_ID INT not null,
    constraint GRANT_CLIENT_ID_FK
        foreign key (CLIENT_ID) references CLIENT (ID)
);
```

##### Add Data for CLIENT and GRANT tables
- append some DDL to the src/main/sesources/data.sql to setup credentials for a  client 
- and add some of the standard OAUTH grants for that client
```

insert into CLIENT (ID, NAME, SECRET, REDIRECT_URI, SCOPE)
values (1, 'client','secret' ,'http://localhost:8181/', 'read');

insert into GRANT (CLIENT_ID, GRANT  ) values (1,  'authorization_code' );
insert into GRANT (CLIENT_ID, GRANT  ) values (1,  'password' );
insert into GRANT (CLIENT_ID, GRANT  ) values (1,  'client_credentials');
insert into GRANT (CLIENT_ID, GRANT  ) values (1,  'refresh_token' );

```

##### verify CLIENT and GRANT tables
- you can rerun your app to make sure the SQL is good
- the app should fail to start if there is an error in the new SQL

##### Create JPA entities for your CLIENT and GRANT tables
this and the following steps are similar to creating a controller  
##### Create a JPA Repository for the Client

##### Add Tests for your Client JPA Repository   
  
##### Create a Client Controller with an GET /client endpoint to list all clients

##### Update Client Controller with an POST /client endpoint to add a new client

##### add Web tests for your controller




# move to milestone 3
##### create a test for your new JpaUserDetailsService
 
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
}
```

##### replace the in-memory user details service with your new JPA backed userDetailsService
- find the java configuration class where you defined a userDetailsService bean and delete it.
- Your new JpaUserDetailsService will automatically be detected and used
```
// DELETE the following config bean
@Override
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
      
```

##### re-run all your unit tests
- UserController
  UserDtoUserController
         UserDto
- you should also be able to connect to the http://localhost:8080/alive endpoint using the same john:12345 credentials you used with the in-memory service
 

##### Troubleshooting
if you've haing problems authenticating usin yoour new JPA userdetailsService then I recommend temporarily enabling
debug in the @EnableWebSecurity annotation e.g 
```
@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

```
you can also enable debug logging in your application.properties
```
logging.level.org.springframework.security=debug
```