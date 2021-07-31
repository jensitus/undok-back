package at.undok.undok.client.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AbstractDto {

    protected UUID id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

}
