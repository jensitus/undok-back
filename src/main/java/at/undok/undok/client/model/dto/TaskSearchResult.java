package at.undok.undok.client.model.dto;

import at.undok.undok.client.model.entity.Task;
import lombok.Data;

import java.util.UUID;

@Data
public class TaskSearchResult {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private UUID clientId;
    private String type = "task";

    public TaskSearchResult(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.clientId = task.getCaseEntity().getClientId();
    }
}
