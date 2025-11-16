package hexlet.code.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskCreateDTO {

    @Size(min = 1)
    @NotBlank
    private String name;

    private Long index;

    private String description;

    @NotNull
    private String status;

    private Long assigneeId;

    private List<Long> taskLabelIds;

}
