package Main.controllers;

import Main.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/news")
public class AdminNewsController {
    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public String showAdminNews(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        model.addAttribute("newsList", newsService.findAllNews());
        return "admin-news";
    }

    @GetMapping("/{id}")
    public String showAdminNewsDetails(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        News news = newsService.findById(id);
        if (news == null) return "redirect:/admin/news";

        model.addAttribute("news", news);
        return "admin-news-details";
    }

    @GetMapping("/edit/{id}")
    public String showEditNewsPage(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        News news = newsService.findById(id);
        if (news == null) return "redirect:/admin/news";

        model.addAttribute("news", news);
        return "admin-edit-news";
    }

    @PostMapping("/add")
    public String addNews(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String publishDate,
            HttpSession session) {

        if (session.getAttribute("admin") == null) return "redirect:/login";

        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setPublishDate(publishDate);
        newsService.saveNews(news);
        return "redirect:/admin/news";
    }

    @PostMapping("/update")
    public String updateNews(
            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String publishDate,
            HttpSession session) {

        if (session.getAttribute("admin") == null) return "redirect:/login";

        News news = newsService.findById(id);
        if (news == null) return "redirect:/admin/news";

        news.setTitle(title);
        news.setContent(content);
        news.setPublishDate(publishDate);
        newsService.saveNews(news);
        return "redirect:/admin/news";
    }

    @PostMapping("/delete")
    public String deleteNews(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        newsService.deleteNewsById(id);
        return "redirect:/admin/news";
    }
}