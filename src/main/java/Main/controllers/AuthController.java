package Main.controllers;

import Main.EmailService;
import Main.User;
import Main.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class AuthController {
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String redirectToLogin() { return "redirect:/login"; }

    @GetMapping("/login")
    public String showLogin() { return "login"; }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        String adminEmail = "admin@kompetens.com";
        String adminPassword = "admin123";

        String cleanEmail = email.trim().toLowerCase();
        String cleanPassword = password.trim();

        if (adminEmail.equals(cleanEmail) && adminPassword.equals(cleanPassword)) {
            session.setAttribute("admin", true);
            return "redirect:/admin";
        }

        User user = userService.findByEmail(cleanEmail);

        if (user != null && passwordEncoder.matches(cleanPassword, user.getPassword())) {
            session.setAttribute("user", user);
            return "redirect:/Home";
        } else {
            model.addAttribute("loginError", "Invalid email or password");
            return "login";
        }
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

        String cleanEmail = email.trim().toLowerCase();
        String cleanPassword = password.trim();

        User existingUser = userService.findByEmail(cleanEmail);
        if (existingUser != null) {
            model.addAttribute("registerError", "User with this email already exists");
            return "login";
        }

        if (cleanPassword.length() < 8) {
            model.addAttribute("registerError", "Password must be at least 8 characters");
            return "login";
        }

        User user = new User();

        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(cleanEmail);
        user.setPhone(phone);
        user.setOrganisation(organisation);
        user.setRole(role);
        user.setMunicipality(municipality);
        user.setPassword(cleanPassword);

        user.setNewsletter(false);
        userService.saveUser(user);

        emailService.sendEmail(
                user.getEmail(),
                "Welcome to Kompetens 5.0",
                "Hi " + user.getFirstName() + ",\n\n" +
                        "You have successfully registered as a member.\n\n" +
                        "Welcome!"
        );

        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}