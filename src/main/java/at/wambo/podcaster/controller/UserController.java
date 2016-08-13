package at.wambo.podcaster.controller;

import at.wambo.podcaster.forms.CreateUserForm;
import at.wambo.podcaster.forms.CreateUserFormValidator;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * @author Martin
 *         13.08.2016
 */
@Controller
public class UserController {
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CreateUserFormValidator createUserFormValidator;

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(createUserFormValidator);
    }

    private User create(CreateUserForm form) {
        User user = new User();
        user.setEmail(form.getEmail());
        user.setName(form.getUsername());
        user.setPwHash(PASSWORD_ENCODER.encode(form.getPassword()));
        return userRepository.save(user);
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public ModelAndView registerPage() {
        return new ModelAndView("register", "form", new CreateUserForm());
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String handleRegister(@Valid @ModelAttribute("form") CreateUserForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            create(form);
        } catch (DataIntegrityViolationException ex) {
            bindingResult.reject("user.exists", "User already exists.");
            return "register";
        }
        return "redirect:/";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView showLoginPage(@RequestParam String error) {
        return new ModelAndView("login", "error", error);
    }
}
