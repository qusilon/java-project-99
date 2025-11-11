package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var users = om.readValue(body, new TypeReference<List<UserDTO>>() {
        });
        var expected = userRepository.findAll();

        assertThatJson(body).isArray();
        assertThat(users).hasSize(expected.size());
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var receivedUser = om.readValue(body, UserDTO.class);

        assertThatJson(body).isNotNull().and(
                json -> json.node("id").isEqualTo(testUser.getId()),
                json -> json.node("firstName").isEqualTo(testUser.getFirstName()),
                json -> json.node("lastName").isEqualTo(testUser.getLastName()),
                json -> json.node("email").isEqualTo(testUser.getEmail())
        );

        assertThat(receivedUser.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(receivedUser.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserCreateModel()).create();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail()).orElseThrow();

        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
        assertThat(user.getEmail()).isEqualTo(data.getEmail());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserUpdateModel()).create();

        mockMvc.perform(put("/api/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId()).orElseThrow();

        if (data.getFirstName().isPresent()) {
            assertThat(updatedUser.getFirstName()).isEqualTo(data.getFirstName().get());
        }
        if (data.getEmail().isPresent()) {
            assertThat(updatedUser.getEmail()).isEqualTo(data.getEmail().get());
        }
    }

    @Test
    public void testDestroy() throws Exception {
        var usersCount = userRepository.count();

        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.count()).isEqualTo(usersCount - 1);
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
