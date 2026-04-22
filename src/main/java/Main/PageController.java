package Main;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@Controller
public class PageController {
    private UserService userService;
    private CourseService courseService;
    private RegistrationService registrationService;

    public PageController(UserService userService, CourseService courseService, RegistrationService registrationService) {
        this.userService = userService;
        this.courseService = courseService;
        this.registrationService = registrationService;
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
            Model model) {

        String adminEmail = "admin@kompetens.com";
        String adminPassword = "admin123";

        if (adminEmail.equals(email) && adminPassword.equals(password)) {
            session.setAttribute("admin", true);
            session.setAttribute("adminEmail", email);
            return "redirect:/admin";
        }
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
}