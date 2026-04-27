package Main;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;


@Controller
public class PageController {
    private final UserService userService;
    private final CourseService courseService;
    private final RegistrationService registrationService;
    private final ContactMessageService contactMessageService;
    private final NewsService newsService;
    public PageController(UserService userService, CourseService courseService, RegistrationService registrationService, ContactMessageService contactMessageService, NewsService newsService) {
        this.userService = userService;
        this.courseService = courseService;
        this.registrationService = registrationService;
        this.contactMessageService = contactMessageService;
        this.newsService = newsService;
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
    public String showAdmin(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        List<User> allUsers = userService.findAllUsers();
        List<Course> allCourses = courseService.findAllCourses();
        List<Registration> allRegistrations = registrationService.getAllRegistrations();

        Map<Long, Long> memberRegistrationCounts = allRegistrations.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> r.getUser().getId(),
                        java.util.stream.Collectors.counting()
                ));

        model.addAttribute("members", allUsers);
        model.addAttribute("totalMembers", allUsers.size());
        model.addAttribute("totalCourses", allCourses.size());
        model.addAttribute("totalRegistrations", allRegistrations.size());
        model.addAttribute("memberRegistrationCounts", memberRegistrationCounts);
        return "admin";
    }

    @PostMapping("/admin/delete-member")
    public String deleteMember(@RequestParam("id") Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/login";
        }
        userService.deleteUserById(id);

        return "redirect:/admin";

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
        model.addAttribute("registrations", registrationService.getRegistrationsByUser(user));
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
            RedirectAttributes redirectAttributes) {

        String adminEmail = "admin@kompetens.com";
        String adminPassword = "admin123";

        String cleanEmail = email.trim().toLowerCase();
        String cleanPassword = password.trim();

        if (adminEmail.equals(cleanEmail) && adminPassword.equals(cleanPassword)) {
            session.setAttribute("admin", true);
            return "redirect:/admin";
        }

        User user = userService.findByEmail(cleanEmail);

        if (user != null && user.getPassword().trim().equals(cleanPassword)) {
            session.setAttribute("user", user);
            return "redirect:/Home";
        }

        redirectAttributes.addFlashAttribute("loginError", "Invalid email or password");
        return "redirect:/login";
    }

    @GetMapping("/browse-courses")
    public String showBrowseCourses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.findAllCourses());
        List<Registration> registrations = registrationService.getRegistrationsByUser(user);
        List<Long> registeredCourseIds = registrations.stream()
                .map(r -> r.getCourse().getId())
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("registeredCourseIds", registeredCourseIds);
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
    @PostMapping("/profile/delete")
    public String deleteAccount(HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        userService.deleteUserById(user.getId());

        session.invalidate(); // loggar ut direkt

        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    @GetMapping("/admin/courses")
    public String showCourses(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        model.addAttribute("courses", courseService.findAllCourses());
        return "admin-courses";
    }

    @PostMapping("/admin/courses/add")
    public String addCourse(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) String registrationDeadline,
            @RequestParam(required = false) String endDate,
            HttpSession session) {

        if (session.getAttribute("admin") == null) return "redirect:/login";

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setDate(date);
        course.setLocation(location);
        course.setMaxParticipants(maxParticipants);
        course.setInstructor(instructor);
        course.setRegistrationDeadline(registrationDeadline);
        course.setEndDate(endDate);
        courseService.saveCourse(course);
        return "redirect:/admin/courses";
    }

    @PostMapping("/admin/courses/delete")
    public String deleteCourse(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        courseService.deleteCourseById(id);
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/register")
    public String registerForCourse(@RequestParam Long courseId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Course course = courseService.findById(courseId);
        if (course != null) {
            registrationService.registerUserForCourse(user, course);
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
    @GetMapping("/admin/member/{id}")
    public String showMemberDetails(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        User member = userService.findById(id);
        List<Registration> registrations = registrationService.getRegistrationsByUser(member);
        model.addAttribute("member", member);
        model.addAttribute("registrations", registrations);
        return "admin-member";
    }
    @GetMapping("/admin/course/{id}")
    public String showCourseDetails(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        Course course = courseService.findById(id);
        if (course == null) return "redirect:/admin/courses";

        List<Registration> registrations = registrationService.getRegistrationsByCourse(course);

        model.addAttribute("course", course);
        model.addAttribute("registrations", registrations);
        model.addAttribute("participantCount", registrations.size());

        return "admin-course";
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
    @GetMapping("/admin/messages")
    public String showAdminMessages(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        model.addAttribute("messages", contactMessageService.findAllMessages());
        return "admin-messages";
    }
    @GetMapping("/admin/message/{id}")
    public String showAdminMessageDetails(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        ContactMessage message = contactMessageService.findById(id);
        if (message == null) return "redirect:/admin/messages";

        model.addAttribute("message", message);
        return "admin-message";
    }

    @PostMapping("/profile/change-password")
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
    @GetMapping("/admin/course/edit/{id}")
    public String showEditCoursePage(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        Course course = courseService.findById(id);
        if (course == null) return "redirect:/admin/courses";

        model.addAttribute("course", course);
        return "admin-edit-course";
    }

    @PostMapping("/admin/course/update")
    public String updateCourse(
            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) String registrationDeadline,
            HttpSession session) {

        if (session.getAttribute("admin") == null) return "redirect:/login";

        Course course = courseService.findById(id);
        if (course == null) return "redirect:/admin/courses";

        course.setTitle(title);
        course.setDescription(description);
        course.setDate(date);
        course.setEndDate(endDate);
        course.setLocation(location);
        course.setMaxParticipants(maxParticipants);
        course.setInstructor(instructor);
        course.setRegistrationDeadline(registrationDeadline);

        courseService.saveCourse(course);

        return "redirect:/admin/courses";
    }

    @PostMapping("/admin/message/delete")
    public String deleteMessage(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        contactMessageService.deleteMessageById(id);
        return "redirect:/admin/messages";
    }
    @PostMapping("/admin/delete-course")
    public String deleteCourseAlt(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        courseService.deleteCourseById(id);
        return "redirect:/admin/courses";
    }
    @GetMapping("/admin/news")
    public String showAdminNews(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        model.addAttribute("newsList", newsService.findAllNews());
        return "admin-news";
    }

    @PostMapping("/admin/news/add")
    public String addNews(@RequestParam String title,
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

    @PostMapping("/admin/news/delete")
    public String deleteNews(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        newsService.deleteNewsById(id);
        return "redirect:/admin/news";
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
    @GetMapping("/admin/news/edit/{id}")
    public String showEditNewsPage(@PathVariable Long id,
                                   HttpSession session,
                                   Model model) {

        if (session.getAttribute("admin") == null)
            return "redirect:/login";

        News news = newsService.findById(id);
        if (news == null)
            return "redirect:/admin/news";

        model.addAttribute("news", news);

        return "admin-edit-news";
    }
    @PostMapping("/admin/news/update")
    public String updateNews(@RequestParam Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam String publishDate,
                             HttpSession session) {

        if (session.getAttribute("admin") == null)
            return "redirect:/login";

        News news = newsService.findById(id);
        if (news == null)
            return "redirect:/admin/news";

        news.setTitle(title);
        news.setContent(content);
        news.setPublishDate(publishDate);

        newsService.saveNews(news);

        return "redirect:/admin/news";
    }
    @GetMapping("/admin/news/{id}")
    public String showAdminNewsDetails(@PathVariable Long id,
                                       HttpSession session,
                                       Model model) {

        if (session.getAttribute("admin") == null)
            return "redirect:/login";

        News news = newsService.findById(id);
        if (news == null)
            return "redirect:/admin/news";

        model.addAttribute("news", news);

        return "admin-news-details";
    }
}