package at.wambo.podcaster.controller;

import at.wambo.podcaster.configuration.CurrentUser;
import at.wambo.podcaster.forms.CreateUserForm;
import at.wambo.podcaster.forms.CreateUserFormValidator;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * @author Martin
 *         13.08.2016
 */

// TODO make me a RestController, remove form stuff
@Controller
public class UserController {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final CreateUserFormValidator createUserFormValidator;

    @Autowired
    public UserController(CreateUserFormValidator createUserFormValidator, UserRepository userRepository) {
        Assert.notNull(createUserFormValidator);
        Assert.notNull(userRepository);
        this.createUserFormValidator = createUserFormValidator;
        this.userRepository = userRepository;
    }

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(this.createUserFormValidator);
    }

    private User create(CreateUserForm form) {
        User user = new User();
        user.setEmail(form.getEmail());
        user.setName(form.getUsername());
        user.setPwHash(PASSWORD_ENCODER.encode(form.getPassword()));
        return this.userRepository.save(user);
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public ModelAndView registerPage() {
        return new ModelAndView("registerPage", "form", new CreateUserForm());
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public Object handleRegister(@Valid @ModelAttribute("form") CreateUserForm form, BindingResult bindingResult) {
        User user;
        if (bindingResult.hasErrors()) {
            logger.debug("Found errors in form: {}", form);
            return "registerPage";
        }
        try {
            user = create(form);
        } catch (DataIntegrityViolationException ex) {
            bindingResult.reject("user.exists", "User already exists.");
            logger.info("User alreadys exists: {}", form);
            return "registerPage";
        }
        logger.debug("Successfully created user {}", user);
        return "redirect:/login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ModelAndView showLoginPage(@RequestParam String error) {
        return new ModelAndView("login", "error", error);
    }

    @RequestMapping(path = "/api/user", method = RequestMethod.GET)
    public @ResponseBody User getUserInfo() {
        return ((CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

}
