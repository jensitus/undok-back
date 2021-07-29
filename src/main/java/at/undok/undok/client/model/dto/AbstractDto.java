package at.undok.undok.client.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class AbstractDto {

    protected UUID id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

}
