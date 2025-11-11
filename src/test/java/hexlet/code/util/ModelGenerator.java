package hexlet.code.util;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import lombok.Getter;
import org.instancio.Select;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {

    @Autowired
    private Faker faker;

    private Model<User> userModel;
    private Model<UserCreateDTO> userCreateModel;
    private Model<UserUpdateDTO> userUpdateModel;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password())
                .toModel();

        userCreateModel = Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(UserCreateDTO::getPassword), () -> faker.internet().password())
                .toModel();

        userUpdateModel = Instancio.of(UserUpdateDTO.class)
                .supply(Select.field(UserUpdateDTO::getFirstName), () -> JsonNullable.of(faker.name().firstName()))
                .supply(Select.field(UserUpdateDTO::getLastName), () -> JsonNullable.of(faker.name().lastName()))
                .supply(Select.field(UserUpdateDTO::getEmail), () -> JsonNullable.of(faker.internet().emailAddress()))
                .supply(Select.field(UserUpdateDTO::getPassword), () -> JsonNullable.of(faker.internet().password()))
                .toModel();
    }
}
