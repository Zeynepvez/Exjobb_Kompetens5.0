package Main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/home")
    public String showHome() {
        return "Home";
    }

    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }
}