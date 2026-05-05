package Main.controllers;

import Main.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {
    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping("/contact")
    public String showContact(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "contact";
    }

    @PostMapping("/contact/send")
    public String sendContactMessage(
            @RequestParam String subject,
            @RequestParam String message,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setSenderName(user.getFirstName() + " " + user.getLastName());
        contactMessage.setSenderEmail(user.getEmail());
        contactMessage.setSubject(subject);
        contactMessage.setMessage(message);

        contactMessageService.saveMessage(contactMessage);
        return "redirect:/contact";
    }
}