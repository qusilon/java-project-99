package hexlet.code.dto.taskStatus;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDTO {

    @NotBlank
    private JsonNullable<String> name;

    @NotBlank
    private JsonNullable<String> slug;

}
