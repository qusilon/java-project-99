package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements ApplicationRunner {

    public static final Map<String, String> DEFAULT_STATUSES = Map.ofEntries(
            Map.entry("Draft", "draft"),
            Map.entry("ToReview", "to_review"),
            Map.entry("ToBeFixed", "to_be_fixed"),
            Map.entry("ToPublish", "to_publish"),
            Map.entry("Published", "published")
    );

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            var admin = new UserCreateDTO();
            admin.setFirstName("Main");
            admin.setLastName("Admin");
            admin.setEmail(email);
            admin.setPassword("qwerty");
            userService.createUser(admin);
        }

        if (taskStatusRepository.count() == 0) {
            List<TaskStatus> statuses = DEFAULT_STATUSES.entrySet().stream()
                    .map(entry -> {
                        TaskStatus status = new TaskStatus();
                        status.setName(entry.getKey());
                        status.setSlug(entry.getValue());
                        return status;
                    })
                    .toList();

            taskStatusRepository.saveAll(statuses);
        }
    }
}
