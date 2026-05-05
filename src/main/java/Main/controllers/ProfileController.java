package Main.controllers;

import Main.User;
import Main.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
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
        if (user == null) return "redirect:/login";

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

    @PostMapping("/delete")
    public String deleteAccount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        userService.deleteUserById(user.getId());
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        String cleanCurrentPassword = currentPassword.trim();
        String cleanNewPassword = newPassword.trim();
        String cleanConfirmPassword = confirmPassword.trim();

        if (!user.getPassword().trim().equals(cleanCurrentPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("passwordError", "Current password is incorrect");
            return "profile";
        }

        if (cleanNewPassword.length() < 8) {
            model.addAttribute("user", user);
            model.addAttribute("passwordError", "New password must be at least 8 characters");
            return "profile";
        }

        if (!cleanNewPassword.equals(cleanConfirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("passwordError", "New passwords do not match");
            return "profile";
        }

        user.setPassword(cleanNewPassword);
        userService.saveUser(user);

        User updatedUser = userService.findByEmail(user.getEmail());
        session.setAttribute("user", updatedUser);

        model.addAttribute("user", updatedUser);
        model.addAttribute("passwordSuccess", "Password updated successfully");
        return "profile";
    }
}