package at.undok.undok.client.controller;

import at.undok.undok.client.api.TaskApi;
import at.undok.undok.client.model.dto.TaskDto;
import at.undok.undok.client.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskService taskService;

    @Override
    public ResponseEntity<TaskDto> createTask(TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Override
    public ResponseEntity<TaskDto> getTaskById(UUID id) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @Override
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @Override
    public ResponseEntity<List<TaskDto>> getTasksByCaseId(UUID caseId) {
        List<TaskDto> tasks = taskService.getTasksByCaseId(caseId);
        return ResponseEntity.ok(tasks);
    }

    @Override
    public ResponseEntity<List<TaskDto>> getActiveTasks() {
        return ResponseEntity.ok(taskService.getActiveTasks());
    }

    @Override
    public ResponseEntity<TaskDto> updateTask(UUID id, TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @Override
    public ResponseEntity<Void> deleteTask(UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
