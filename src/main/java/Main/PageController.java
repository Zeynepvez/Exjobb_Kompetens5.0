package Main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/Home")
    public String showHome() {
        return "Home";
    }

    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }
    @GetMapping("/browse-courses")
    public String showBrowseCourses() {
        return "browse-courses";
    }
}