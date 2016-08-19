package at.wambo.podcaster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Martin
 *         17.08.2016
 */
@Controller
public class HomeController {
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home() {
        return "index";
    }
}
