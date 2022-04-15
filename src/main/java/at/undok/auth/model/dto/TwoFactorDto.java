package at.undok.auth.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TwoFactorDto {
    private UUID id;
    private UUID userId;
    private String token;
    private LocalDateTime expiration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
