package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TaskDTO {

    private Long id;

    private String name;

    private Long index;

    private String description;

    private String status;

    private Long assigneeId;

    private LocalDateTime createdAt;

    private List<Long> taskLabelIds;
}
