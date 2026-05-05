package Main.controllers;

import Main.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {
    private final UserService userService;
    private final CourseService courseService;
    private final RegistrationService registrationService;

    public AdminController(UserService userService, CourseService courseService, RegistrationService registrationService) {
        this.userService = userService;
        this.courseService = courseService;
        this.registrationService = registrationService;
    }

    @GetMapping("/admin")
    public String showAdmin(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        List<User> allUsers = userService.findAllUsers();
        List<Course> allCourses = courseService.findAllCourses();
        List<Registration> allRegistrations = registrationService.getAllRegistrations();

        Map<Long, Long> memberRegistrationCounts = allRegistrations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getUser().getId(),
                        Collectors.counting()
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
        if (session.getAttribute("admin") == null) return "redirect:/login";
        userService.deleteUserById(id);
        return "redirect:/admin";
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

    @GetMapping("/admin/export/members")
    public void exportMembers(HttpSession session, HttpServletResponse response) throws Exception {
        if (session.getAttribute("admin") == null) {
            response.sendRedirect("/login");
            return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=members.csv");

        List<User> users = userService.findAllUsers();
        PrintWriter writer = response.getWriter();
        writer.println("First Name,Last Name,Email,Phone,Organisation,Role,Municipality");

        for (User u : users) {
            writer.println(
                    csvField(u.getFirstName()) + "," +
                            csvField(u.getLastName()) + "," +
                            csvField(u.getEmail()) + "," +
                            csvField(u.getPhone()) + "," +
                            csvField(u.getOrganisation()) + "," +
                            csvField(u.getRole()) + "," +
                            csvField(u.getMunicipality())
            );
        }
        writer.flush();
    }

    @GetMapping("/admin/export/course/{id}")
    public void exportCourseParticipants(@PathVariable Long id, HttpSession session, HttpServletResponse response) throws Exception {
        if (session.getAttribute("admin") == null) {
            response.sendRedirect("/login");
            return;
        }

        Course course = courseService.findById(id);
        if (course == null) {
            response.sendRedirect("/admin/courses");
            return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=participants-" + id + ".csv");

        List<Registration> registrations = registrationService.getRegistrationsByCourse(course);
        PrintWriter writer = response.getWriter();
        writer.println("First Name,Last Name,Email,Phone,Organisation,Role,Municipality");

        for (Registration r : registrations) {
            User u = r.getUser();
            writer.println(
                    csvField(u.getFirstName()) + "," +
                            csvField(u.getLastName()) + "," +
                            csvField(u.getEmail()) + "," +
                            csvField(u.getPhone()) + "," +
                            csvField(u.getOrganisation()) + "," +
                            csvField(u.getRole()) + "," +
                            csvField(u.getMunicipality())
            );
        }
        writer.flush();
    }

    private String csvField(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}