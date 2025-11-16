package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskSpecification taskSpecification;

    public List<TaskDTO> getAllTasks(
            String titleCont,
            Long assigneeId,
            String status,
            Long labelId
    ) {
        Specification<Task> spec = taskSpecification.build(
                titleCont,
                assigneeId,
                status,
                labelId
        );

        return taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO getTaskById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + "not found"));
        return taskMapper.map(task);
    }

    public TaskDTO createTask(TaskCreateDTO taskCreateDTO) {
        var task = taskMapper.map(taskCreateDTO);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO updateTask(TaskUpdateDTO taskUpdateDTO, Long id) {
        var taskStatus = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + "not found"));
        taskMapper.update(taskUpdateDTO, taskStatus);
        taskRepository.save(taskStatus);
        return taskMapper.map(taskStatus);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
