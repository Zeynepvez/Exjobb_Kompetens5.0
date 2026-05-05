package Main.controllers;

import Main.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminMessageController {
    private final ContactMessageService contactMessageService;

    public AdminMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping("/messages")
    public String showAdminMessages(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        model.addAttribute("messages", contactMessageService.findAllMessages());
        return "admin-messages";
    }

    @GetMapping("/message/{id}")
    public String showAdminMessageDetails(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        ContactMessage message = contactMessageService.findById(id);
        if (message == null) return "redirect:/admin/messages";

        model.addAttribute("message", message);
        return "admin-message";
    }

    @PostMapping("/message/delete")
    public String deleteMessage(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        contactMessageService.deleteMessageById(id);
        return "redirect:/admin/messages";
    }
}