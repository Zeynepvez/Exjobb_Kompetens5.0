package Main;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final UserService userService;

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
    public String showHome(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "Home";
    }

    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";
    }

    @GetMapping("/profile")
    public String showProfile() {
        return "profile";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        User user = userService.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/Home";
        }
        model.addAttribute("loginError", "Fel email eller lösenord");
        return "login";
    }
}