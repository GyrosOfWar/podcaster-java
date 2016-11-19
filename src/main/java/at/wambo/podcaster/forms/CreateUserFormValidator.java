package at.wambo.podcaster.forms;

import at.wambo.podcaster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Martin
 *         13.08.2016
 */
@Component
public class CreateUserFormValidator implements Validator {
    private final UserRepository userRepository;

    @Autowired
    public CreateUserFormValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CreateUserRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateUserRequest form = (CreateUserRequest) target;
        if (form.hasNullValues()) {
            errors.reject("value.empty", "One of the values in the form is empty.");
            return;
        }

        validatePasswords(errors, form);
        validateEmail(errors, form);
    }


    private void validatePasswords(Errors errors, CreateUserRequest form) {
        if (!form.getPassword().equals(form.getPasswordRepeated())) {
            errors.reject("password.no_match", "Passwords do not match.");
        }
    }

    private void validateEmail(Errors errors, CreateUserRequest form) {
        if (this.userRepository.findByEmail(form.getEmail()).isPresent()) {
            errors.reject("user.exists", "User already exists.");
        }
    }
}