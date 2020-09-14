package com.manning.ssia.milestone.domain;




import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserValidationTests {
    private static Validator validator;

    @BeforeEach
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
