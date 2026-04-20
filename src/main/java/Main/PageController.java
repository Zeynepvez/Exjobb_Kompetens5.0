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

    @GetMapping("/Home.html")
    public String showHome() {
        return "Home";
    }

    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }

    @GetMapping("/contact.html")
    public String contact() {
        return "contact";
    @GetMapping("/my-courses")
    public String showMyCourses() {
        return "my-courses";

    }
    @GetMapping("/profile")
    public String showProfile() {
        return "profile";
    }
}