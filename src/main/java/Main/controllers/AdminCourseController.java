package Main.controllers;

import Main.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminCourseController {
    private final CourseService courseService;
    private final RegistrationService registrationService;

    public AdminCourseController(CourseService courseService, RegistrationService registrationService) {
        this.courseService = courseService;
        this.registrationService = registrationService;
    }

    @GetMapping("/courses")
    public String showCourses(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        model.addAttribute("courses", courseService.findAllCourses());
        return "admin-courses";
    }

    @PostMapping("/courses/add")
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

    @PostMapping("/courses/delete")
    public String deleteCourse(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        courseService.deleteCourseById(id);
        return "redirect:/admin/courses";
    }

    @PostMapping("/delete-course")
    public String deleteCourseAlt(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/login";
        courseService.deleteCourseById(id);
        return "redirect:/admin/courses";
    }

    @GetMapping("/course/edit/{id}")
    public String showEditCoursePage(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        Course course = courseService.findById(id);
        if (course == null) return "redirect:/admin/courses";

        model.addAttribute("course", course);
        return "admin-edit-course";
    }

    @PostMapping("/course/update")
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

    @GetMapping("/course/{id}")
    public String showAdminCourseDetails(@PathVariable Long id, HttpSession session, Model model) {
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