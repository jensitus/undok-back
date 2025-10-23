package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.TaskDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/service/undok")
@PreAuthorize("hasRole('USER')")
public interface TaskApi {

    @PostMapping("/api/tasks")
    ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto);

    @GetMapping("/api/tasks/{id}")
    ResponseEntity<TaskDto> getTaskById(@PathVariable UUID id);

    @GetMapping("/api/tasks")
    ResponseEntity<List<TaskDto>> getAllTasks();

    @GetMapping("/api/tasks/case/{caseId}")
    ResponseEntity<List<TaskDto>> getTasksByCaseId(@PathVariable UUID caseId);

    @PutMapping("/api/tasks/{id}")
    ResponseEntity<TaskDto> updateTask(@PathVariable UUID id, @RequestBody TaskDto taskDto);

    @DeleteMapping("/api/tasks/{id}")
    ResponseEntity<Void> deleteTask(@PathVariable UUID id);

}
