package Main;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    public RegistrationService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public void registerUserForCourse(User user, Course course) {
        if (!registrationRepository.existsByUserAndCourse(user, course)) {
            Registration registration = new Registration();
            registration.setUser(user);
            registration.setCourse(course);
            registrationRepository.save(registration);
        }
    }

    public List<Registration> getRegistrationsByUser(User user) {
        return registrationRepository.findByUser(user);
    }
    public void unregisterUserFromCourse(User user, Course course) {
        List<Registration> registrations = registrationRepository.findByUser(user);
        registrations.stream()
                .filter(r -> r.getCourse().getId().equals(course.getId()))
                .findFirst()
                .ifPresent(registrationRepository::delete);
    }
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }
    public List<Registration> getRegistrationsByCourse(Course course) {
        return registrationRepository.findByCourse(course);
    }
}