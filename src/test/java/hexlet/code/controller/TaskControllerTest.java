package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static hexlet.code.component.DataInitializer.ADMIN_EMAIL;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Task testTask;

    private User testUser;

    private TaskStatus testTaskStatus;

    private Label testLabel;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject(ADMIN_EMAIL));

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).
                defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity()).build();

        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        testTask = Instancio.of(modelGenerator.getTaskModel())
                .create();
        testLabel = Instancio.of(modelGenerator.getLabelModel())
                .create();

        labelRepository.save(testLabel);
        userRepository.save(testUser);
        taskStatusRepository.save(testTaskStatus);

        testLabel.getTasks().add(testTask);
        testTask.getLabels().add(testLabel);
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);

    }

    @AfterEach
    public void clean() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        taskRepository.save(testTask);
        var response = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        List<TaskDTO> taskDTOS = om.readValue(body, new TypeReference<>() {
        });
        var actual = taskDTOS.stream().map(taskMapper::map).toList();
        var expected = taskRepository.findAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        taskRepository.save(testTask);
        var response = mockMvc.perform(get("/api/tasks/" + testTask.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();

        assertThatJson(body).isNotNull().and(
                json -> json.node("id").isEqualTo(testTask.getId()),
                json -> json.node("name").isEqualTo(testTask.getName()),
                json -> json.node("index").isEqualTo(testTask.getIndex()),
                json -> json.node("description").isEqualTo(testTask.getDescription()),
                json -> json.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                json -> json.node("assigneeId").isEqualTo(testTask.getAssignee().getId())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = taskMapper.map(testTask);

        var request = post("/api/tasks").with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isCreated());

        var task = taskRepository.findByName(testTask.getName()).orElse(null);

        assertNotNull(task);
        assertThat(task.getName()).isEqualTo(testTask.getName());
        assertThat(task.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(testTask.getTaskStatus().getSlug());
        assertThat(task.getAssignee().getId()).isEqualTo(testTask.getAssignee().getId());
    }

    @Test
    public void testUpdate() throws Exception {
        taskRepository.save(testTask);
        var data = new HashMap<>();
        data.put("name", "newName");
        data.put("description", "newDescription");

        var request = put("/api/tasks/" + testTask.getId())
                .with(token).contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).orElseThrow();

        assertThat(task.getName()).isEqualTo(("newName"));
        assertThat(task.getDescription()).isEqualTo(("newDescription"));
    }

    @Test
    public void testDestroy() throws Exception {
        taskRepository.save(testTask);
        var taskCount = taskRepository.count();

        mockMvc.perform(delete("/api/tasks/" + testTask.getId()).with(token))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.count()).isEqualTo(taskCount - 1);
        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }

    @Test
    public void testFilter() throws Exception {

        Task matching = Instancio.of(modelGenerator.getTaskModel()).create();
        matching.setName("Important task");
        matching.setAssignee(testUser);
        matching.setTaskStatus(testTaskStatus);
        taskRepository.save(matching);


        Task notMatching = Instancio.of(modelGenerator.getTaskModel()).create();
        notMatching.setName("Another one");
        notMatching.setAssignee(null);
        notMatching.setTaskStatus(testTaskStatus);
        taskRepository.save(notMatching);

        var response = mockMvc.perform(
                        get("/api/tasks?titleCont=important&assigneeId=" + testUser.getId())
                                .with(token)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<TaskDTO> result = om.readValue(response, new TypeReference<>() {
        });

        assertThat(result)
                .hasSize(1)
                .allMatch(dto ->
                        dto.getName().equals("Important task") && dto.getAssigneeId().equals(testUser.getId())
                );
    }
}
