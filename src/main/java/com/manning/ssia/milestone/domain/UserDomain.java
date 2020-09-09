package com.manning.ssia.milestone.domain;

import com.manning.ssia.milestone.jpa.Authority;
import com.manning.ssia.milestone.jpa.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonComponent
@Data
@NoArgsConstructor
public class UserDomain {
    private int id;
    @NotBlank(message = "the username must be defined")
    private String username;
    @NotBlank(message = "the password must be defined")
    private String password;
    @Size(min=1, message = "at least one authority must  be defined")
    private List<String> authorities;

    public UserDomain(int id, @NotBlank(message = "the username must be defined")
            String username, @NotBlank(message = "the password must be defined")
            String password, @Size(min = 1, message = "at least one authority must  be defined")
            List<String> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public UserDomain(int id,
                      @NotBlank(message = "the username must be defined")
            String username, @NotBlank(message = "the password must be defined")
                              String password, @Size(min = 1, message = "at least one authority must  be defined")
                              String authority) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = Collections.singletonList(authority);
    }

    public UserDomain(User user) {

        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        if (user.getAuthorities() !=null && user.getAuthorities().size()>0) {
            this.authorities = user.getAuthorities()
                    .stream()
                    .map(Authority::getAuthority)
                    .collect(Collectors.toList());
        }
    }


}
