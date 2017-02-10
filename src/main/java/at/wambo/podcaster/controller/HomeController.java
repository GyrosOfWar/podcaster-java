package at.wambo.podcaster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Martin
 *         17.08.2016
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @RequestMapping("/app/**")
    public String app() {
        return "forward:/index.html";
    }
}
