package Main;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {
    private UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/Home")
    public String showHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "Home";
    }

    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }

    @GetMapping("/contact")
    public String showContact() {
        return "contact";
    }


    @GetMapping("/my-courses")
    public String showMyCourses() {
        return "my-courses";

    }
    @GetMapping("/profile")
    public String showProfile() {
        return "profile";
    }
    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        User user = userService.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/Home";
        }

        model.addAttribute("loginError", "Invalid email or password");
        return "login";
    }

    @GetMapping("/browse-courses")
    public String showBrowseCourses() {
        return "browse-courses";
    }
}