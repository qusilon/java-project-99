package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    public abstract TaskDTO map(Task task);

    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusToEntity")
    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "userToEntity")
    public abstract Task map(TaskDTO dto);


    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusToEntity")
    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "userToEntity")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusToEntity")
    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "userToEntity")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task task);

    @Named("statusToEntity")
    public TaskStatus statusToEntity(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
    }

    @Named("userToEntity")
    public User userToEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
