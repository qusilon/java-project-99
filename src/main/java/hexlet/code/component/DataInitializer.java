package hexlet.code.component;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            var admin = new User();
            admin.setFirstName("Main");
            admin.setLastName("Admin");
            admin.setEmail(email);
            admin.setPasswordDigest("qwerty");
            userService.createUser(admin);
        }
    }
}
