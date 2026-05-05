package Main.controllers;

import Main.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public String showUserNews(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("newsList", newsService.findAllNews());
        return "news";
    }

    @GetMapping("/news/{id}")
    public String showNewsDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        News news = newsService.findById(id);
        if (news == null) return "redirect:/news";

        model.addAttribute("user", user);
        model.addAttribute("news", news);
        return "news-details";
    }
}