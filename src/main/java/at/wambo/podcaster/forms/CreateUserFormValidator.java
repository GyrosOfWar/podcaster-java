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
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CreateUserForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateUserForm form = (CreateUserForm) target;
        validatePasswords(errors, form);
        validateEmail(errors, form);
    }

    private void validatePasswords(Errors errors, CreateUserForm form) {
        if (!form.getPassword().equals(form.getPasswordRepeated())) {
            errors.reject("password.no_match", "Passwords do not match.");
        }
    }

    private void validateEmail(Errors errors, CreateUserForm form) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            errors.reject("user.exists", "User already exists.");
        }
    }
}
