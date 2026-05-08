package Main;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {

        return userRepository.findAll();
    }

    public void deleteUserById(Long id) {

        userRepository.deleteById(id);
    }
    public User findById(Long id) {

        return userRepository.findById(id).orElse(null);
    }

}