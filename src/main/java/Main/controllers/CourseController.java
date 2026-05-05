package Main.controllers;

import Main.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final RegistrationService registrationService;
    private final NewsService newsService;
    private final EmailService emailService;

    public CourseController(CourseService courseService, RegistrationService registrationService, NewsService newsService, EmailService emailService) {
        this.courseService = courseService;
        this.registrationService = registrationService;
        this.newsService = newsService;
        this.emailService = emailService;
    }

    @GetMapping("/Home")
    public String showHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("newsList", newsService.findAllNews());
        return "Home";
    }

    @GetMapping("/my-courses")
    public String showMyCourses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("registrations", registrationService.getRegistrationsByUser(user));
        return "my-courses";
    }

    @GetMapping("/browse-courses")
    public String showBrowseCourses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.findAllCourses());

        List<Long> registeredCourseIds = registrationService.getRegistrationsByUser(user)
                .stream()
                .map(r -> r.getCourse().getId())
                .collect(Collectors.toList());

        model.addAttribute("registeredCourseIds", registeredCourseIds);
        return "browse-courses";
    }

    @GetMapping("/courses/{id}")
    public String showCourseDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Course course = courseService.findById(id);
        if (course == null) return "redirect:/Home";

        boolean isRegistered = registrationService.getRegistrationsByUser(user)
                .stream()
                .anyMatch(r -> r.getCourse().getId().equals(id));

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("isRegistered", isRegistered);
        return "course-details";
    }

    @PostMapping("/courses/register")
    public String registerForCourse(@RequestParam Long courseId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Course course = courseService.findById(courseId);
        if (course != null) {
            registrationService.registerUserForCourse(user, course);
            emailService.sendEmail(
                    user.getEmail(),
                    "Course Registration Confirmation",
                    "Hi " + user.getFirstName() + ",\n\n" +
                            "You are now registered for:\n" +
                            course.getTitle() + "\n\n" +
                            "Date: " + course.getDate()
            );
        }
        return "redirect:/browse-courses";
    }

    @PostMapping("/courses/unregister")
    public String unregisterFromCourse(@RequestParam Long courseId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Course course = courseService.findById(courseId);
        if (course != null) {
            registrationService.unregisterUserFromCourse(user, course);
        }
        return "redirect:/my-courses";
    }
}