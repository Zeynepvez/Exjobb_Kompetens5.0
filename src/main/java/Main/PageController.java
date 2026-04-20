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


    @PostMapping("/register")
    public String registerUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "organisation", required = false) String organisation,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "municipality", required = false) String municipality,
            @RequestParam("password") String password,
            Model model) {

        User existingUser = userService.findByEmail(email);
        if (existingUser != null) {
            model.addAttribute("registerError", "User with this email already exists");
            return "login";
        }
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setOrganisation(organisation);
        user.setRole(role);
        user.setMunicipality(municipality);
        user.setPassword(password);

        userService.saveUser(user);

        return "redirect:/login";
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
    public String showContact(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "contact";
    }


    @GetMapping("/my-courses")
    public String showMyCourses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "my-courses";

    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
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
    public String showBrowseCourses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "browse-courses";
    }
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) String organisation,
            @RequestParam(required = false) String role,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setMunicipality(municipality);
        user.setOrganisation(organisation);
        user.setRole(role);

        userService.saveUser(user);
        session.setAttribute("user", user);
        return "redirect:/profile";
    }
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}