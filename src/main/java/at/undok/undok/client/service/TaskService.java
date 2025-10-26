package at.undok.undok.client.service;

import at.undok.auth.model.dto.UserDto;
import at.undok.auth.service.UserService;
import at.undok.undok.client.mapper.inter.TaskMapper;
import at.undok.undok.client.model.dto.TaskDto;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Task;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.TaskRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskRepo taskRepo;
    private final CaseRepo caseRepo;
    private final UserService userService;

    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);

        // Set the Case relationship
        if (taskDto.getCaseId() != null) {
            Case caseEntity = caseRepo.findById(taskDto.getCaseId())
                                      .orElseThrow(() -> new RuntimeException("Case not found with id: " + taskDto.getCaseId()));
            task.setCaseEntity(caseEntity);
        }

        UserDto currentUser = userService.getCurrentUser();
        task.setCreatedBy(currentUser.getUsername());

        task.setCreatedAt(LocalDateTime.now());
        Task savedTask = taskRepo.save(task);
        return taskMapper.toDto(savedTask);
    }

    public TaskDto updateTask(UUID id, TaskDto taskDto) {
        Task existingTask = taskRepo.findById(id)
                                    .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        taskMapper.updateEntityFromDto(taskDto, existingTask);

        // Update Case relationship if needed
        if (taskDto.getCaseId() != null &&
                (existingTask.getCaseEntity() == null ||
                        !existingTask.getCaseEntity().getId().equals(taskDto.getCaseId()))) {
            Case caseEntity = caseRepo.findById(taskDto.getCaseId())
                                      .orElseThrow(() -> new RuntimeException("Case not found with id: " + taskDto.getCaseId()));
            existingTask.setCaseEntity(caseEntity);
        }

        existingTask.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskRepo.save(existingTask);
        return taskMapper.toDto(updatedTask);
    }

    public TaskDto getTaskById(UUID id) {
        Task task = taskRepo.findById(id)
                            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return taskMapper.toDto(task);
    }

    public List<TaskDto> getAllTasks() {
        return taskRepo.findAll().stream()
                       .map(taskMapper::toDto)
                       .collect(Collectors.toList());
    }

    public List<TaskDto> getTasksByCaseId(UUID caseId) {
        List<Task> tasks = taskRepo.findByCaseEntity_Id(caseId);
        return tasks.stream()
                    .map(taskMapper::toDto)
                    .collect(Collectors.toList());
    }

    public List<TaskDto> getActiveTasks() {
        List<String> activeStatuses = List.of("Open", "In Progress");
        List<Task> tasks = taskRepo.findByStatusIn(activeStatuses);
        return tasks.stream()
                    .map(taskMapper::toDto)
                    .collect(Collectors.toList());
    }

    public void deleteTask(UUID id) {
        if (!taskRepo.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepo.deleteById(id);
    }

}
